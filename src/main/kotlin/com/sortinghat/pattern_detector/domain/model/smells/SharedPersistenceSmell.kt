package com.sortinghat.pattern_detector.domain.model.smells

import com.sortinghat.pattern_detector.domain.behaviors.Smell
import com.sortinghat.pattern_detector.domain.model.Database
import com.sortinghat.pattern_detector.domain.model.Service
import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual

@Serializable
data class SharedPersistenceSmell (
    @Contextual val database: String,
    @Contextual val services: List<String>
) : Smell {
    companion object {
        fun from(database: Database, services: List<Service> ) = SharedPersistenceSmell(
            database.name,
            services.map { it.name }
        )
    }
}
