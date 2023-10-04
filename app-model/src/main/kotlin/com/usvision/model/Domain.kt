package com.usvision.model

data class CompanySystem(
    override val name: String
) : SystemOfSystems() {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
        subsystems.forEach { it.accept(visitor) }
    }
}

abstract class Operation(
    open val description: String = ""
)

interface Database : Visitable {
    val id: String?
    val description: String
}

data class PostgreSQL(
    override val description: String = "PostgreSQL database",
    override val id: String? = null
) : Database {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    override fun equals(other: Any?): Boolean {
        return if (other !is PostgreSQL) false
        else if (id == null || other.id == null) description == other.description
        else id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

data class Microservice(
    override val name: String
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

data class RestEndpoint(
    val httpVerb: String,
    val path: String,
    override val description: String = ""
) : Operation(description)

data class MessageChannel(
    val name: String,
    val id: String? = null
) {
    override fun equals(other: Any?): Boolean {
        return if (other !is MessageChannel) false
        else if (id == null || other.id == null) name == other.name
        else id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}