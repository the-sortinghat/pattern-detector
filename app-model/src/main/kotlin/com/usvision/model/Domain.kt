package com.usvision.model

data class CompanySystem(
    override val name: String
) : SystemOfSystems() {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

abstract class Operation(
    open val description: String = ""
)

interface Database {
    val id: String?
    val description: String
}

data class PostgreSQL(
    override val description: String = "PostgreSQL database",
    override val id: String? = null
) : Database {
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

    override fun getConsumedOperations(): Set<Operation> = consumedOperations

    override fun getDatabases(): Set<Database> = databases

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

data class RestEndpoint(
    val httpVerb: String,
    val path: String,
    override val description: String = ""
) : Operation(description)

