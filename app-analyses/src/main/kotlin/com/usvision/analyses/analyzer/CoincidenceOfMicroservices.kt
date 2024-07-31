package com.usvision.analyses.analyzer

import com.usvision.model.domain.Microservice
import com.usvision.model.domain.databases.Database
import com.usvision.model.domain.MessageChannel
import com.usvision.model.domain.operations.Operation
import com.usvision.model.visitor.Visitable


class CoincidenceOfMicroservices : RelationshipAnalyzer() {
    private val msCoincidences: MutableMap<Visitable, MutableList<Pair<Relationship, String>>> = mutableMapOf()
    private val producers = mutableMapOf<Operation, MutableList<Microservice>>()
    private val consumers = mutableMapOf<Operation, MutableList<Microservice>>()
    private val publishers: MutableMap<MessageChannel, MutableList<Microservice>> = mutableMapOf()
    private val subscribers: MutableMap<MessageChannel, MutableList<Microservice>> = mutableMapOf()
    private val msDatabase: MutableMap<Database, MutableList<Microservice>> = mutableMapOf()

    override fun getResults(): Map<Visitable, Relationship> {
        return msCoincidences.mapValues { entry ->
            entry.value.first().first
        }
    }

    fun getDetailedResults(): Map<Visitable, List<Pair<Relationship, String>>> {
        return msCoincidences.mapValues { entry ->
            entry.value.toList()
        }
    }

    override fun visit(microservice: Microservice) {
        // Check coincidences between microservices in the scope of sync dependencies
        microservice.getConsumedOperations().forEach { op ->
            if (op !in consumers) {
                consumers[op] = mutableListOf()
            }
            consumers[op]?.add(microservice)
        }
        consumers.forEach { (_, microservices) ->
            if (microservices.size > 1) {
                microservices.forEach { consumer ->
                    microservices.filter { it != consumer }.forEach { otherConsumer ->
                        val relationship = Relationship(with = otherConsumer)
                        msCoincidences.getOrPut(consumer) { mutableListOf() }.add(Pair(relationship, "sync"))
                    }
                }
            }
        }
        microservice.getExposedOperations().forEach { op ->
            if (op !in producers) {
                producers[op] = mutableListOf()
            }
            producers[op]?.add(microservice)
        }
        producers.forEach { (_, microservices) ->
            if (microservices.size > 1) {
                microservices.forEach { producer ->
                    microservices.filter { it != producer }.forEach { otherProducer ->
                        val relationship = Relationship(with = otherProducer)
                        msCoincidences.getOrPut(producer) { mutableListOf() }.add(Pair(relationship, "sync"))
                    }
                }
            }
        }

        // Check coincidences between microservices in the scope of async dependencies
        microservice.getPublishChannels().forEach { channel ->
            if (channel !in this.publishers) {
                this.publishers[channel] = mutableListOf()
            }
            this.publishers[channel]?.add(microservice)
        }
        publishers.forEach { (_, microservices) ->
            if (microservices.size > 1) {
                microservices.forEach { publisher ->
                    microservices.filter { it != publisher }.forEach { otherPublisher ->
                        val relationship = Relationship(with = otherPublisher)
                        msCoincidences.getOrPut(publisher) { mutableListOf() }.add(Pair(relationship, "async"))
                    }
                }
            }
        }
        microservice.getSubscribedChannels().forEach { channel ->
            if (channel !in this.subscribers) {
                this.subscribers[channel] = mutableListOf()
            }
            this.subscribers[channel]?.add(microservice)
        }
        subscribers.forEach { (_, microservices) ->
            if (microservices.size > 1) {
                microservices.forEach { subscriber ->
                    microservices.filter { it != subscriber }.forEach { otherSubscriber ->
                        val relationship = Relationship(with = otherSubscriber)
                        msCoincidences.getOrPut(subscriber) { mutableListOf() }.add(Pair(relationship, "async"))
                    }
                }
            }
        }

        // Check coincidences between microservices in the scope of database dependencies
        microservice.getDatabases().forEach { database ->
            if (database !in this.msDatabase) {
                this.msDatabase[database] = mutableListOf()
            }
            this.msDatabase[database]?.add(microservice)
        }
        msDatabase.forEach { (_, microservices) ->
            if (microservices.size > 1) {
                microservices.forEach { database ->
                    microservices.filter { it != database }.forEach { otherDatabase ->
                        val relationship = Relationship(with = otherDatabase)
                        msCoincidences.getOrPut(database) { mutableListOf() }.add(Pair(relationship, "database"))
                    }
                }
            }
        }

    }
}