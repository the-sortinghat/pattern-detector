package com.sortinghat.pattern_detector.api

import com.sortinghat.pattern_detector.domain.model.patterns.APIComposition
import com.sortinghat.pattern_detector.domain.model.patterns.AsyncMessage
import com.sortinghat.pattern_detector.domain.model.patterns.DatabasePerService
import com.sortinghat.pattern_detector.domain.model.patterns.SingleServicePerHost
import kotlinx.serialization.Serializable

@Serializable
data class PatternsPresent(
    val databasePerService: Set<DatabasePerService>,
    val singleServicePerHost: Set<SingleServicePerHost>,
    val apiCompositions: Set<APIComposition>,
    val asyncMessages: Set<AsyncMessage>
)
