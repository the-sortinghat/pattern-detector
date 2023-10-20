package com.usvision.model.domain

import com.usvision.model.domain.databases.Database
import com.usvision.model.domain.operations.Operation
import com.usvision.model.systemcomposite.SystemOfComponents
import com.usvision.model.visitor.Visitor
import kotlinx.serialization.Serializable

@Serializable
data class Microservice(
    override val name: String,
    var module: Module = Module.createWithId()
) : SystemOfComponents {
    private val exposedOperations: MutableSet<Operation> = mutableSetOf()
    private val consumedOperations: MutableSet<Operation> = mutableSetOf()
    private val databases: MutableSet<Database> = mutableSetOf()
    private val publishChannels: MutableSet<MessageChannel> = mutableSetOf()
    private val subscribedChannels: MutableSet<MessageChannel> = mutableSetOf()

    override fun getExposedOperations(): Set<Operation> = exposedOperations

    override fun exposeOperation(operation: Operation) {
        exposedOperations.add(operation)
    }

    override fun consumeOperation(operation: Operation) {
        consumedOperations.add(operation)
    }

    override fun addDatabaseConnection(database: Database) {
        databases.add(database)
    }

    override fun addPublishChannel(channel: MessageChannel) {
        publishChannels.add(channel)
    }

    override fun addSubscribedChannel(channel: MessageChannel) {
        subscribedChannels.add(channel)
    }

    override fun getConsumedOperations(): Set<Operation> = consumedOperations

    override fun getDatabases(): Set<Database> = databases

    override fun getPublishChannels(): Set<MessageChannel> = publishChannels

    override fun getSubscribedChannels(): Set<MessageChannel> = subscribedChannels

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}