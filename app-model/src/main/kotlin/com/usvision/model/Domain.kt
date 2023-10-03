package com.usvision.model

data class CompanySystem(
    override val name: String
) : SystemOfSystems() {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

data class Microservice(
    override val name: String
) : SystemOfComponents {
    private val exposedOperations: MutableSet<Operation> = mutableSetOf()
    private val consumedOperations: MutableSet<Operation> = mutableSetOf()

    override fun getExposedOperations(): Set<Operation> = exposedOperations

    override fun exposeOperation(operation: Operation) {
        exposedOperations.add(operation)
    }

    override fun consumeOperation(operation: Operation) {
        consumedOperations.add(operation)
    }

    override fun getConsumedOperations(): Set<Operation> = consumedOperations

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

data class RestEndpoint(
    val httpVerb: String,
    val path: String,
    override val description: String = ""
) : Operation(description)

