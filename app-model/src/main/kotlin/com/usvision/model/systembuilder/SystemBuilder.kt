package com.usvision.model.systembuilder

import com.usvision.model.domain.CompanySystem
import com.usvision.model.systemcomposite.System

class SystemBuilderException(message: String)
    : RuntimeException(message)

class SystemBuilder(
    private val parent: SystemBuilder? = null
) {
    private lateinit var rootName: String
    private lateinit var subsystems: MutableSet<System>

    private fun fluentInterface(instance: SystemBuilder = this, implementation: SystemBuilder.() -> Unit): SystemBuilder {
        implementation()
        return instance
    }

    fun setName(name: String) = fluentInterface { rootName = name }

    fun addSubsystems() =
        fluentInterface(SystemBuilder(this)) { subsystems = mutableSetOf() }

    fun endSubsystems(): SystemBuilder {
        if (parent == null)
            throw SystemBuilderException("Attempted to close an environment that had not being opened")

        return parent
    }

    fun thatHasMicroservices() = MicroserviceBuilder(this)

    fun build(): System {
        return CompanySystem(rootName).also { root ->
            if (this::subsystems.isInitialized)
                subsystems.forEach(root::addSubsystem)
        }
    }
}

class MicroserviceBuilder(
    private val parent: SystemBuilder? = null
) {
    fun endMicroservices(): SystemBuilder {
        if (parent == null)
            throw RuntimeException()

        return parent
    }
}