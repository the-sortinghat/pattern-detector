package com.sortinghat.pattern_detector.domain.model.smells

data class SmellDetections(
    val sharedPersistence: Set<SharedPersistenceSmell>,
    val cyclicDependencies: Set<CyclicDependenciesSmell>
)
