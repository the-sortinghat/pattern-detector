package com.usvision.analyses.detector

import com.usvision.model.domain.Microservice

data class CqrsInstance(
    val query: Microservice,
    val commands: Set<Microservice>
) : ArchitectureInsight {
    companion object {
        fun of(query: Microservice, commands: List<Microservice>): CqrsInstance {
            return CqrsInstance(query, commands.toSet())
        }
    }
}
