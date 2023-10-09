package com.usvision.model.systemcomposite

import com.usvision.model.domain.databases.Database
import com.usvision.model.domain.MessageChannel
import com.usvision.model.domain.operations.Operation

abstract class SystemOfSystems() : System {
    protected open val subsystems: MutableSet<System> = mutableSetOf()

    fun addSubsystem(system: System) = subsystems.add(system)

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