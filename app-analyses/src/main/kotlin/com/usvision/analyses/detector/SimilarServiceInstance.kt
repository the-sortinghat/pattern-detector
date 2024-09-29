package com.usvision.analyses.detector

import com.usvision.model.domain.Microservice
import kotlinx.serialization.Serializable

@Serializable
data class SimilarServiceInstance(
    val memberNames: Set<String>,
    val coincidenceType: Set<String>,
    val members: Set<Microservice>
) : ArchitectureInsight {
    companion object {
        fun of(pair: Pair<Microservice, Microservice>) = SimilarServiceInstance(
            memberNames = setOf(pair.first.name, pair.second.name),
            coincidenceType = emptySet(),
            members = setOf(pair.first, pair.second)
        )
    }
}