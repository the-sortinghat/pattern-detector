package com.usvision.creation.command

import java.util.UUID

interface CommandHistoryDataProvider {
    fun getSessionCommands(session: UUID): List<Command>

    fun saveCommand(command: Command)
}