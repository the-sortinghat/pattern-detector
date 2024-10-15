package com.usvision.web.configuration

import com.usvision.reports.ReportSupervisor
import com.usvision.reports.SystemRepository
import io.ktor.server.application.*
import io.ktor.server.config.*


fun Application.configureReports(systemRepository: SystemRepository): ReportSupervisor {
    val presets = parsePresetsConfig(environment.config.config("reports.presets"))

    return ReportSupervisor(
        systemRepository,
        presets = presets
    )
}

fun parsePresetsConfig(presetsConfig: ApplicationConfig): Map<String, Set<String>> {
    val presets = mutableMapOf<String, Set<String>>()
    val configMap = presetsConfig.toMap()

    configMap.keys.forEach { presetName ->
        val maybeDetectorsList = configMap[presetName]!!

        if (maybeDetectorsList !is List<*>) presets[presetName] = emptySet()
        else presets[presetName] = maybeDetectorsList.map { it.toString() }.toSet()
    }

    return presets
}
