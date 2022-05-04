package com.sortinghat.pattern_detector.api

import com.sortinghat.pattern_detector.domain.model.patterns.DatabasePerService
import com.sortinghat.pattern_detector.domain.model.patterns.SingleServicePerHost
import kotlinx.serialization.Serializable

@Serializable
data class PatternsInSystemPayload(
    val system: String,
    val patterns: PatternsPresent
) {
    companion object {
        fun create(
            system: String,
            databasePerServices: Set<DatabasePerService>,
            singleServicePerHost: Set<SingleServicePerHost>
        ): PatternsInSystemPayload {
            return PatternsInSystemPayload(
                system = system,
                patterns = PatternsPresent(
                    databasePerService = databasePerServices,
                    singleServicePerHost = singleServicePerHost
                )
            )
        }
    }
}
