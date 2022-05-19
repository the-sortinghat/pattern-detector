package com.sortinghat.pattern_detector.domain.model.patterns

import com.sortinghat.pattern_detector.domain.behaviors.Pattern
import com.sortinghat.pattern_detector.domain.model.Service
import kotlinx.serialization.Serializable

fun extractName(service: Service) = service.name

@Serializable
data class CQRS(
    val queryService: String,
    val commandServices: Set<String>
) : Pattern {
    companion object {
        fun from(query: Service, commands: Set<Service>): CQRS {
            return CQRS(query.name, commands.map(::extractName).toSet() )
        }
    }
}
