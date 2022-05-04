package com.sortinghat.pattern_detector.domain.model.patterns

data class Detections(
    val databasePerServices: Set<DatabasePerService>,
    val singleServicePerHosts: Set<SingleServicePerHost>
)
