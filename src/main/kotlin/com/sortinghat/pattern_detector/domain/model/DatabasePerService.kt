package com.sortinghat.pattern_detector.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DatabasePerService(
    val service: String,
    val database: String
) {
    companion object {
        fun from(service: Service, database: Database) = DatabasePerService(
            service.name,
            database.name
        )
    }
}
