package com.sortinghat.pattern_detector.api

import com.sortinghat.pattern_detector.domain.model.patterns.DatabasePerService
import kotlinx.serialization.Serializable

@Serializable
data class PatternsInSystemPayload(
    val system: String,
    val patterns: PatternsPresent
) {
    companion object {
        fun create(system: String, databasePerServices: Set<DatabasePerService>): PatternsInSystemPayload {
            return PatternsInSystemPayload(
                system = system,
                patterns = PatternsPresent(
                    databasePerService = databasePerServices
                )
            )
        }
    }
}
