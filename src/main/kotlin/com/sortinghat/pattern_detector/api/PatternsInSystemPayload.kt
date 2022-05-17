package com.sortinghat.pattern_detector.api

import com.sortinghat.pattern_detector.domain.model.patterns.Detections
import kotlinx.serialization.Serializable

@Serializable
data class PatternsInSystemPayload(
    val system: String,
    val patterns: PatternsPresent
) {
    companion object {
        fun from(system: String, detections: Detections): PatternsInSystemPayload {
            return PatternsInSystemPayload(
                system,
                patterns = PatternsPresent(
                    detections.databasePerServices,
                    detections.singleServicePerHosts,
                    detections.apiCompostions,
                    detections.asyncMessages
                )
            )
        }
    }
}
