package com.usvision.model

interface System : Visitable {
    val name: String

    fun getExposedOperations(): Set<Operation>
    fun getConsumedOperations(): Set<Operation>
}

abstract class SystemOfSystems() : System {
    protected open val subsystems: MutableSet<System> = mutableSetOf()

    fun addSubsystem(system: System) = subsystems.add(system)

    fun removeSubsystem(system: System) = subsystems.remove(system)

    fun getSubsystemSet(): Set<System> = subsystems

    override fun getExposedOperations() = subsystems
        .fold(emptySet<Operation>()) { acc, curr ->
            acc + curr.getExposedOperations()
        }

    override fun getConsumedOperations() = subsystems
        .fold(emptySet<Operation>()) { acc, curr ->
            acc + curr.getConsumedOperations()
        }
}

interface SystemOfComponents : System {
    fun exposeOperation(operation: Operation)
    fun consumeOperation(operation: Operation)
}

abstract class Operation(
    open val description: String = ""
)