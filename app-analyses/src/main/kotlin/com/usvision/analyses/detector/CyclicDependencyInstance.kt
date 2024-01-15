package com.usvision.analyses.detector

import com.usvision.model.domain.Microservice

data class CyclicDependencyInstance(
    val members: Set<Microservice>
) : ArchitectureInsight {
    companion object {
        fun fromParentRelations(
            root: Microservice,
            parentOf: Map<Microservice, Microservice>
        ): CyclicDependencyInstance {
            val members = parseParents(emptySet(), root, parentOf)
            return CyclicDependencyInstance(members)
        }

        private fun parseParents(currentSet: Set<Microservice>, child: Microservice, parentOf: Map<Microservice, Microservice>): Set<Microservice> {
            if (child in currentSet) return currentSet
            return parseParents(currentSet + child, parentOf[child]!!, parentOf)
        }
    }
}
