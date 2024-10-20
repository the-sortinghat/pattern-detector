package com.usvision.web.configuration

import com.usvision.creation.SystemAggregateStorage
import com.usvision.creation.SystemCreator
import io.ktor.server.application.*

fun Application.configureSystemCreator(
    systemAggregateStorage: SystemAggregateStorage
) = SystemCreator(systemAggregateStorage)