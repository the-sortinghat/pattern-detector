package com.usvision.creation.command

import com.usvision.model.systembuilder.SystemBuilder
import java.util.UUID

class CommandInvoker(
    private val commandHistoryDataProvider: CommandHistoryDataProvider
) {

    private val commandList: List<Command> = listOf(CreateCompanySystemCommand(UUID.randomUUID()))

    fun invokeCommand(commandName: String, parameter: String?, session: UUID = UUID.randomUUID()) {
        val command = getCorrectCommand(commandName, session, parameter)

        commandHistoryDataProvider.getSessionCommands(session)
            .plus(command)
            .fold(null, ::executeCommandUsingPreviousSystemBuilder)

        commandHistoryDataProvider.saveCommand(command)
    }

    private fun executeCommandUsingPreviousSystemBuilder(
        systemBuilder: SystemBuilder?,
        command: Command
    ) = command.execute(systemBuilder)

    private fun getCorrectCommand(commandName: String, session: UUID, parameter: String?): Command {
        return commandList.first { command -> command.commandName == commandName }.createCopy(
            session = session,
            parameter = parameter
        )
    }
}