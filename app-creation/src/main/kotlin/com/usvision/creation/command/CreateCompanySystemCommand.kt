package com.usvision.creation.command

import com.usvision.model.systembuilder.CompanySystemBuilder
import com.usvision.model.systembuilder.SystemBuilder
import java.util.UUID

data class CreateCompanySystemCommand(
    override val session: UUID,
    override val parameter: String? = null
) : Command() {
    override val commandName: String
        get() = "create-company"

    override fun execute(systemBuilder: SystemBuilder?): SystemBuilder {
        systemBuilder?.also {
            throw Exception("System already exists")
        }

        return CompanySystemBuilder()
    }

    override fun createCopy(session: UUID, parameter: String?): Command = this.copy(
        session = session,
        parameter = parameter
    )


}