package com.sortinghat.pattern_detector.domain.services

import com.sortinghat.pattern_detector.domain.behaviors.PatternDetector
import com.sortinghat.pattern_detector.domain.behaviors.Visitable
import com.sortinghat.pattern_detector.domain.behaviors.Visitor
import com.sortinghat.pattern_detector.domain.model.*
import com.sortinghat.pattern_detector.domain.model.patterns.AsyncMessage

class AsyncMessageDetector : Visitor, PatternDetector {

    private val visited = mutableSetOf<Visitable>()
    private val consumerCandidates = mutableSetOf<Service>()
    private val producerCandidates = mutableSetOf<Service>()
    private val producerToChannels = mutableMapOf<Service, MutableSet<MessageChannel>>()
    private val channelToConsumer = mutableMapOf<MessageChannel, MutableSet<Service>>()

    override fun getResults(): Set<AsyncMessage> {
        val producerToConsumers = mutableMapOf<Service, MutableSet<Service>>()
        producerToChannels.forEach { (producer, setOfChannels) ->
            if (producer !in producerCandidates) return@forEach

            setOfChannels.forEach { channel ->
                if (producerToConsumers[producer] == null) producerToConsumers[producer] = mutableSetOf()
                channelToConsumer[channel]?.forEach { consumer ->
                    if (consumer !in consumerCandidates) return@forEach
                    producerToConsumers[producer]!!.add(consumer)
                }
            }
        }

        val instances = mutableSetOf<AsyncMessage>()

        producerToConsumers.forEach { (producer, setOfConsumers) ->
            setOfConsumers.forEach { consumer -> instances.add(AsyncMessage.from(producer, consumer)) }
        }

        return instances
    }

    override fun visit(service: Service) {
        if (service in visited) return

        visited.add(service)

        if (service.get(Metrics.ASYNC_DEPENDENCY) > 0) consumerCandidates.add(service)
        if (service.get(Metrics.ASYNC_IMPORTANCE) > 0) producerCandidates.add(service)

        service.channelsPublished.forEach { targetChannel ->
            if (producerToChannels[service] == null) producerToChannels[service] = mutableSetOf()
            producerToChannels[service]!!.add(targetChannel)
        }

        service.channelsListening.forEach { sourceChannel ->
            if (channelToConsumer[sourceChannel] == null) channelToConsumer[sourceChannel] = mutableSetOf()
            channelToConsumer[sourceChannel]!!.add(service)
        }

        service.children().forEach { it.accept(visitor = this)}
    }

    override fun visit(operation: Operation) {
        if (operation in visited) return

        visited.add(operation)

        operation.children().forEach { it.accept(visitor = this)}
    }

    override fun visit(database: Database) {
        if (database in visited) return

        visited.add(database)

        database.children().forEach { it.accept(visitor = this)}
    }

    override fun visit(usage: DatabaseUsage) {
        if (usage in visited) return

        visited.add(usage)

        usage.children().forEach { it.accept(visitor = this)}
    }

    override fun visit(module: Module) {
        if (module in visited) return

        visited.add(module)

        module.children().forEach { it.accept(visitor = this)}
    }

    override fun visit(channel: MessageChannel) {
        if (channel in visited) return

        visited.add(channel)

        channel.children().forEach { it.accept(visitor = this)}
    }

    override fun visit(dependency: ServiceDependency) {
        if (dependency in visited) return

        visited.add(dependency)
        dependency.children().forEach { it.accept(visitor = this) }
    }

}