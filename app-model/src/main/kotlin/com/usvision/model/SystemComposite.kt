package com.usvision.model

interface System : Visitable {
    val name: String

    fun getExposedOperations(): Set<Operation>
    fun getConsumedOperations(): Set<Operation>
    fun getDatabases(): Set<Database>
    fun getPublishChannels(): Set<MessageChannel>
    fun getSubscribedChannels(): Set<MessageChannel>
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

    override fun getDatabases() = subsystems
        .fold(emptySet<Database>()) { acc, curr ->
            acc + curr.getDatabases()
        }

    override fun getPublishChannels() = subsystems
        .fold(emptySet<MessageChannel>()) { acc, curr ->
            acc + curr.getPublishChannels()
        }

    override fun getSubscribedChannels() = subsystems
        .fold(emptySet<MessageChannel>()) { acc, curr ->
            acc + curr.getSubscribedChannels()
        }
}

interface SystemOfComponents : System {
    fun exposeOperation(operation: Operation)
    fun consumeOperation(operation: Operation)
    fun addDatabaseConnection(database: Database)
    fun addPublishChannel(channel: MessageChannel)
    fun addSubscribedChannel(channel: MessageChannel)
}
