package com.usvision.analyses.analyzer

import com.usvision.model.domain.MessageChannel
import com.usvision.model.domain.Microservice
import com.usvision.model.visitor.Visitable

class AsyncDependenciesOfMicroservice : RelationshipsAnalyzer() {
    private val publishers: MutableMap<MessageChannel, MutableSet<Microservice>> = mutableMapOf()
    private val subscribers: MutableMap<MessageChannel, MutableSet<Microservice>> = mutableMapOf()

    override fun getResults(): Map<Visitable, Set<Relationship>> {
        val dependencies = mutableMapOf<Visitable, MutableSet<Relationship>>()

        this.subscribers.keys.forEach { channel ->
            val dependents = this.subscribers[channel]!!
            val publishers = this.publishers[channel] ?: emptySet()

            dependents.forEach { dependent ->
                publishers.forEach { publisher ->
                    val relationship = Relationship(with = publisher)
                    dependencies.getOrPut(dependent) { mutableSetOf() }.add(relationship)
                }
            }
        }

        return dependencies
    }

    override fun visit(microservice: Microservice) {
        microservice.getPublishChannels().forEach { pubChannel ->
            if (pubChannel !in this.publishers) {
                this.publishers[pubChannel] = mutableSetOf()
            }

            this.publishers[pubChannel]?.add(microservice)
        }

        microservice.getSubscribedChannels().forEach { subChannel ->
            if (subChannel !in this.subscribers) {
                this.subscribers[subChannel] = mutableSetOf()
            }

            this.subscribers[subChannel]?.add(microservice)
        }
    }
}