package com.sortinghat.pattern_detector.domain.services

import com.sortinghat.pattern_detector.domain.behaviors.Visitable
import com.sortinghat.pattern_detector.domain.behaviors.Visitor
import com.sortinghat.pattern_detector.domain.model.*

class MetricCollector : Visitor {

    private val visited = mutableSetOf<Visitable>()
    private val channelToPublishers = mutableMapOf<MessageChannel, MutableSet<Service>>()
    private val channelToConsumers = mutableMapOf<MessageChannel, MutableSet<Service>>()
    private val publisherToConsumers = mutableMapOf<Service, MutableSet<Service>>()

    override fun visit(service: Service) {
        if (service in visited) return

        visited.add(service)

        service.exposedOperations.forEach { _ -> service.increase(Metrics.OPERATIONS_OF_SERVICE) }
        service.usages.forEach { _ -> service.increase(Metrics.DATABASES_USED_BY_SERVICE) }
        service.consumedOperations.forEach { _ -> service.increase(Metrics.SYNC_DEPENDENCY) }

        service.channelsPublished.forEach { targetChannel ->
            if (channelToPublishers[targetChannel] == null) channelToPublishers[targetChannel] = mutableSetOf()
            channelToPublishers[targetChannel]!!.add(service)

            channelToConsumers[targetChannel]?.forEach { consumer: Service ->
                if (publisherToConsumers[service]?.contains(consumer) == true) return

                if (publisherToConsumers[service] == null) publisherToConsumers[service] = mutableSetOf()
                publisherToConsumers[service]!!.add(consumer)

                service.increase(Metrics.ASYNC_IMPORTANCE)
                consumer.increase(Metrics.ASYNC_DEPENDENCY)
            }
        }

        service.channelsListening.forEach { sourceChannel ->
            if (channelToConsumers[sourceChannel] == null) channelToConsumers[sourceChannel] = mutableSetOf()
            channelToConsumers[sourceChannel]!!.add(service)

            channelToPublishers[sourceChannel]?.forEach { publisher ->
                if (publisherToConsumers[publisher]?.contains(service) == true) return

                if (publisherToConsumers[publisher] == null) publisherToConsumers[publisher] = mutableSetOf()
                publisherToConsumers[publisher]!!.add(service)
                service.increase(Metrics.ASYNC_DEPENDENCY)
                publisher.increase(Metrics.ASYNC_IMPORTANCE)
            }
        }

        service.children().forEach { child ->
            child.accept(visitor = this)
        }
    }

    override fun visit(operation: Operation) {
        if (operation in visited) return

        visited.add(operation)
        operation.children().forEach { it.accept(visitor = this) }
    }

    override fun visit(database: Database) {
        if (database in visited) return

        visited.add(database)

        database.usages.forEach { _ -> database.increase(Metrics.CLIENTS_OF_DATABASE) }

        database.children().forEach { child ->
            child.accept(visitor = this)
        }
    }

    override fun visit(usage: DatabaseUsage) {
        if (usage in visited) return

        visited.add(usage)
        usage.children().forEach { it.accept(visitor = this) }
    }

    override fun visit(module: Module) {
        if (module in visited) return

        visited.add(module)

        module.children().forEach {
            module.increase(Metrics.SERVICES_PER_MODULE)
            it.accept(visitor = this)
        }
    }

    override fun visit(channel: MessageChannel) {
        if (channel in visited) return

        visited.add(channel)
        channel.children().forEach { it.accept(visitor = this) }
    }
}