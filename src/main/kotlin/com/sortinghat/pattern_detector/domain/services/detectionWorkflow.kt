package com.sortinghat.pattern_detector.domain.services

import com.sortinghat.pattern_detector.domain.model.ServiceRepository
import com.sortinghat.pattern_detector.domain.model.patterns.Detections


fun detectionWorkflow(systemSlug: String, serviceRepository: ServiceRepository, thresholds: Map<String, Int>): Detections {
    val services = serviceRepository.findAllOfSystem(systemSlug)

    val metricCollector = MetricCollector()
    services.forEach { it.accept(metricCollector) }

    val dbps = DatabasePerServiceDetector(
        maxOperationsPerService = thresholds["maxOperationsPerService"]!!
    )
    services.forEach { it.accept(dbps) }

    val ssph = SingleServicePerHostDetector(
        maxOperationsPerService = thresholds["maxOperationsPerService"]!!
    )
    services.forEach { it.accept(ssph) }

    val apic = APICompositionDetector(
        maxOperationsPerService = thresholds["maxOperationsPerService"]!!,
        minComposedServices = thresholds["minComposedServices"]!!
    )
    services.forEach { it.accept(apic) }

    val amsg = AsyncMessageDetector()
    services.forEach { it.accept(amsg) }

    val amsgOccurrences = amsg.getResults()

    val cqrs = CQRSDetector(
        asyncMessageOccurrences = amsgOccurrences,
        maxOperationsPerService = thresholds["maxOperationsPerService"]!!
    )
    services.forEach { it.accept(cqrs) }


    return Detections(
        databasePerServices = dbps.getResults(),
        singleServicePerHosts = ssph.getResults(),
        apiCompostions = apic.getResults(),
        asyncMessages = amsgOccurrences,
        cqrs = cqrs.getResults()
    )
}