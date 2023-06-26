package com.sortinghat.pattern_detector.api

import com.sortinghat.pattern_detector.domain.model.smells.SmellDetections
import kotlinx.serialization.Serializable

@Serializable
data class SmellsInSystemPayload(
    val system: String,
    val smells: SmellsPresent
) {
    companion object {
        fun from(system: String, smellDetections: SmellDetections): SmellsInSystemPayload {
            return SmellsInSystemPayload(
                system,
                smells = SmellsPresent(
                    smellDetections.sharedPersistence,
                    smellDetections.cyclicDependencies
                )
            )
        }
    }
}
