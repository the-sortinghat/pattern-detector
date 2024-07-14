package com.usvision.analyses.detector

import com.usvision.model.domain.Microservice
import kotlinx.serialization.Serializable

@Serializable
data class SimilarServiceInstance(
    val members: Set<Microservice>
) : ArchitectureInsight {
    companion object {
        fun fromCoincidences(
            coincidences: Set<Pair<Microservice, Microservice>>
        ): Set<SimilarServiceInstance> {
            return coincidences.map { (microservice1, microservice2) ->
                SimilarServiceInstance(setOf(microservice1, microservice2))
            }.toSet()
        }
    }
}