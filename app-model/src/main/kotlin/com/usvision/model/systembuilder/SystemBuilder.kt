package com.usvision.model.systembuilder

import com.usvision.model.domain.CompanySystem
import com.usvision.model.domain.MessageChannel
import com.usvision.model.domain.Microservice
import com.usvision.model.domain.databases.Database
import com.usvision.model.domain.databases.PostgreSQL
import com.usvision.model.domain.operations.Operation
import com.usvision.model.domain.operations.RestEndpoint
import com.usvision.model.systemcomposite.System

class SystemBuilderException(message: String)
    : RuntimeException(message)

class SystemBuilder(private val parent: SystemBuilder? = null) {
    private lateinit var rootName: String
    private lateinit var subsystems: MutableSet<System>

    private fun fluentInterface(instance: SystemBuilder = this, implementation: SystemBuilder.() -> Unit): SystemBuilder {
        implementation()
        return instance
    }

    fun setName(name: String) = fluentInterface { rootName = name }

    fun addSubsystems() = fluentInterface(SystemBuilder(this)) {
        subsystems = mutableSetOf()
    }

    fun endSubsystems(): SystemBuilder {
        if (parent == null)
            throw SystemBuilderException("Attempted to close an environment that had not being opened")

        if (this::rootName.isInitialized) {
            val system = build()
            parent.addSubsystem(system)
        }

        return parent
    }

    fun thatHasMicroservices(): MicroserviceBuilder {
        subsystems = mutableSetOf()
        return MicroserviceBuilder(this)
    }

    fun addSubsystem(system: System) {
        subsystems.add(system)
    }

    fun addMicroservice(microservice: Microservice) = fluentInterface {
        subsystems.add(microservice)
    }

    fun build(): System {
        return CompanySystem(rootName).also { root ->
            if (this::subsystems.isInitialized)
                subsystems.forEach(root::addSubsystem)
        }
    }
}

class MicroserviceBuilder(private val parent: SystemBuilder? = null) {
    private lateinit var name: String
    private val exposedOperations = mutableSetOf<Operation>()
    private val consumedOperations = mutableSetOf<Operation>()
    private val databases = mutableSetOf<Database>()
    private val channelsPublished = mutableSetOf<MessageChannel>()
    private val channelsSubscribed = mutableSetOf<MessageChannel>()

    private fun fluentInterface(implementation: MicroserviceBuilder.() -> Unit): MicroserviceBuilder {
        implementation()
        return this
    }

    fun endMicroservices(): SystemBuilder {
        if (parent == null)
            throw SystemBuilderException("Attempted to close an environment that had not being opened")

        val microservice = build()
        parent.addMicroservice(microservice)

        return parent
    }

    fun and(): MicroserviceBuilder {
        endMicroservices()
        return MicroserviceBuilder(this.parent)
    }

    fun named(name: String) = fluentInterface {
        this.name = name
    }

    fun oneNamed(name: String) = named(name)

    fun anotherNamed(name: String) = named(name)

    fun exposingRestEndpoint(httpVerb: String, path: String, description: String) = fluentInterface {
        exposedOperations.add(RestEndpoint(httpVerb, path, description))
    }

    fun thatRequestsHttpEndpoint(httpVerb: String, path: String, description: String) = fluentInterface {
        consumedOperations.add(RestEndpoint(httpVerb, path, description))
    }

    fun accessingPostgres(id: String) = fluentInterface {
        databases.add(PostgreSQL(id = id))
    }

    fun thatPublishesTo(channelId: String, channelName: String) = fluentInterface {
        channelsPublished.add(MessageChannel(id = channelId, name = channelName))
    }

    fun thatIsSubscribedTo(channelId: String, channelName: String) = fluentInterface {
        channelsSubscribed.add(MessageChannel(name = channelName, id = channelId))
    }

    fun build(): Microservice {
        return Microservice(name).also { msvc ->
            exposedOperations.forEach(msvc::exposeOperation)
            consumedOperations.forEach(msvc::consumeOperation)
            databases.forEach(msvc::addDatabaseConnection)
            channelsPublished.forEach(msvc::addPublishChannel)
            channelsSubscribed.forEach(msvc::addSubscribedChannel)
        }
    }
}