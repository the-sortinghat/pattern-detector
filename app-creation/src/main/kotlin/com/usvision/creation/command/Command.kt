package com.usvision.creation.command

import com.usvision.model.systembuilder.SystemBuilder
import java.util.UUID

abstract class Command {

    abstract val commandName: String

    abstract val session: UUID

    abstract val parameter: String?

    abstract fun execute(systemBuilder: SystemBuilder?): SystemBuilder

    abstract fun createCopy(
        session: UUID,
        parameter: String? = null
    ): Command
}