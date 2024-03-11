package com.usvision.analyses.detector

import com.usvision.analyses.analyzer.Relationship
import com.usvision.model.domain.Microservice

data class ApiCompositionInstance(
    val composer: Microservice,
    val composees: Set<Microservice>
) : ArchitectureInsight {
    companion object {
        fun of(composer: Microservice, relationships: Set<Relationship>): ApiCompositionInstance {
            val composees = relationships.map { it.with as Microservice }.toSet()
            return ApiCompositionInstance(
                composer, composees
            )
        }
    }
}