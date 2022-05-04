package com.sortinghat.pattern_detector.domain.model

import com.sortinghat.pattern_detector.domain.behaviors.Pattern
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
