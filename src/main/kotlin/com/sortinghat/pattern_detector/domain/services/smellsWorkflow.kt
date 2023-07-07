package com.sortinghat.pattern_detector.domain.services

import com.sortinghat.pattern_detector.domain.model.ServiceRepository
import com.sortinghat.pattern_detector.domain.model.smells.SmellDetections
import com.sortinghat.pattern_detector.domain.model.Service


fun smellsWorkflow(systemSlug: String, serviceRepository: ServiceRepository): SmellDetections {
    val services: List<Service> = serviceRepository.findAllOfSystem(systemSlug)

    val sharedPersistence = SharedPersistenceSmellDetector()
    services.forEach { it.accept(sharedPersistence) }

    val cyclicDetector = CyclicDependenciesSmellDetector(services)
    services.forEach { it.accept(cyclicDetector) }

    return SmellDetections(
        sharedPersistence = sharedPersistence.getResults(),
        cyclicDependencies = cyclicDetector.getResults()
    )
}