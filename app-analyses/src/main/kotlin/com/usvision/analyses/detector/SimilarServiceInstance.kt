package com.usvision.analyses.detector

import com.usvision.model.domain.Microservice
import kotlinx.serialization.Serializable

@Serializable
data class SimilarServiceInstance(
    val members: Set<Microservice>,
    val coincidenceType: Set<String>
) : ArchitectureInsight {
    companion object {
        fun fromCoincidences(
            coincidences: Set<Pair<Microservice, Microservice>>,
            coincidenceType: Set<String>
        ): Set<SimilarServiceInstance> {
            return coincidences.map { (microservice1, microservice2) ->
                SimilarServiceInstance(setOf(microservice1, microservice2), coincidenceType)
            }.toSet()
        }
    }
}