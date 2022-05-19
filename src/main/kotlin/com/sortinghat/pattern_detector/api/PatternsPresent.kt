package com.sortinghat.pattern_detector.api

import com.sortinghat.pattern_detector.domain.model.patterns.*
import kotlinx.serialization.Serializable

@Serializable
data class PatternsPresent(
    val databasePerService: Set<DatabasePerService>,
    val singleServicePerHost: Set<SingleServicePerHost>,
    val apiCompositions: Set<APIComposition>,
    val asyncMessages: Set<AsyncMessage>,
    val cqrs: Set<CQRS>
)
