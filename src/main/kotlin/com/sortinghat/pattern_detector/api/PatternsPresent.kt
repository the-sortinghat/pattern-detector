package com.sortinghat.pattern_detector.api

import com.sortinghat.pattern_detector.domain.model.patterns.DatabasePerService
import kotlinx.serialization.Serializable

@Serializable
data class PatternsPresent(
    val databasePerService: Set<DatabasePerService>
)
