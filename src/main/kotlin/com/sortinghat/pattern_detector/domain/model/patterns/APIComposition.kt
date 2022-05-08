package com.sortinghat.pattern_detector.domain.model.patterns

import com.sortinghat.pattern_detector.domain.behaviors.Pattern
import com.sortinghat.pattern_detector.domain.model.Service
import kotlinx.serialization.Serializable

@Serializable
data class APIComposition(
    val service: String
) : Pattern {
    companion object {
        fun from(service: Service): APIComposition {
            return APIComposition(service = service.name)
        }
    }
}
