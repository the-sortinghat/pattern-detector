package com.sortinghat.pattern_detector.domain.services

import com.sortinghat.pattern_detector.domain.model.ServiceRepository
import com.sortinghat.pattern_detector.domain.model.patterns.Detections


fun detectionWorkflow(systemSlug: String, serviceRepository: ServiceRepository): Detections {
    val services = serviceRepository.findAllOfSystem(systemSlug)

    val visitors = mapOf(
        "metrics" to MetricCollector(),
        "dbps" to DatabasePerServiceDetector(),
        "ssph" to SingleServicePerHostDetector()
    )

    visitors.values.forEach { visitor ->
        services.forEach { it.accept(visitor) }
    }

    return Detections(
        databasePerServices = (visitors["dbps"] as DatabasePerServiceDetector).getResults(),
        singleServicePerHosts = (visitors["ssph"] as SingleServicePerHostDetector).getResults()
    )
}