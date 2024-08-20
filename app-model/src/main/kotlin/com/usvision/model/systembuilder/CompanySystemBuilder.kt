package com.usvision.model.systembuilder

import com.usvision.model.domain.CompanySystem
import com.usvision.model.domain.MessageChannel
import com.usvision.model.domain.Microservice
import com.usvision.model.domain.databases.Database
import com.usvision.model.domain.databases.PostgreSQL
import com.usvision.model.domain.operations.Operation
import com.usvision.model.domain.operations.RestEndpoint
import com.usvision.model.systemcomposite.System

interface SystemBuilder

class SystemBuilderException(message: String)
    : RuntimeException(message)

class CompanySystemBuilder(private val parent: CompanySystemBuilder? = null): SystemBuilder {
    private var rootName: String? = null
    private var subsystems: MutableSet<System> = mutableSetOf()

    private fun fluentInterface(instance: CompanySystemBuilder = this, implementation: CompanySystemBuilder.() -> Unit): CompanySystemBuilder {
        implementation()
        return instance
    }

    fun setName(name: String) = fluentInterface {
        this.rootName?.also {
            throw SystemBuilderException("System already has a name: $rootName")
        }
        rootName = name
    }

    fun addSubsystems(): CompanySystemBuilder {
        return CompanySystemBuilder(this)
    }

    fun and(): CompanySystemBuilder {
        endSubsystems()
        return CompanySystemBuilder(this.parent)
    }

    fun endSubsystems(): CompanySystemBuilder {
        if (parent == null)
            throw SystemBuilderException("Attempted to close an environment that had not being opened")

        val system = build()
        parent.addSubsystem(system)

        return parent
    }

    fun thatHasMicroservices(): MicroserviceBuilder {
        return MicroserviceBuilder(this)
    }

    fun addSubsystem(system: System) {
        subsystems.add(system)
    }

    fun addMicroservice(microservice: Microservice) = fluentInterface {
        subsystems.add(microservice)
    }

    fun build(): System {
        return this.rootName?.let { rootName ->
            CompanySystem(rootName).also { root ->
                subsystems.forEach(root::addSubsystem)
            }
        } ?: throw SystemBuilderException("Attempted to build a System with no name")
    }
}

class MicroserviceBuilder(private val parent: CompanySystemBuilder? = null): SystemBuilder  {
    private var name: String? = null
    private val exposedOperations = mutableSetOf<Operation>()
    private val consumedOperations = mutableSetOf<Operation>()
    private val databases = mutableSetOf<Database>()
    private val channelsPublished = mutableSetOf<MessageChannel>()
    private val channelsSubscribed = mutableSetOf<MessageChannel>()

    private fun fluentInterface(implementation: MicroserviceBuilder.() -> Unit): MicroserviceBuilder {
        implementation()
        return this
    }

    fun endMicroservices(): CompanySystemBuilder {
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
        this.name?.also {
            throw SystemBuilderException("Microservice already has a name: $name")
        }

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
        return this.name?.let { name ->
            Microservice(name).also { msvc ->
                exposedOperations.forEach(msvc::exposeOperation)
                consumedOperations.forEach(msvc::consumeOperation)
                databases.forEach(msvc::addDatabaseConnection)
                channelsPublished.forEach(msvc::addPublishChannel)
                channelsSubscribed.forEach(msvc::addSubscribedChannel)
            }
        } ?: throw SystemBuilderException("Attempted to build a Microservice with no name")
    }
}