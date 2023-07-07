package com.sortinghat.pattern_detector.domain.model.smells

import com.sortinghat.pattern_detector.domain.behaviors.Smell
import com.sortinghat.pattern_detector.domain.model.Service
import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual

@Serializable
data class CyclicDependenciesSmell(
    @Contextual val service: String,
    @Contextual val cyclicDependencies: List<String>
) : Smell {
    companion object {
        fun from(service: Service, cyclicDependencies: List<Service>) = CyclicDependenciesSmell(
            service.name,
            cyclicDependencies.map { it.name }
        )
    }
}