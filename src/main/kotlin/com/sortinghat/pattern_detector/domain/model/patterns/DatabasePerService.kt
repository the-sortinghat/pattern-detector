package com.sortinghat.pattern_detector.domain.model.patterns

import com.sortinghat.pattern_detector.domain.behaviors.Pattern
import com.sortinghat.pattern_detector.domain.model.Database
import com.sortinghat.pattern_detector.domain.model.Service
import kotlinx.serialization.Serializable

@Serializable
data class DatabasePerService (
    val service: String,
    val database: String
) : Pattern {
    companion object {
        fun from(service: Service, database: Database) = DatabasePerService(
            service.name,
            database.name
        )
    }
}
