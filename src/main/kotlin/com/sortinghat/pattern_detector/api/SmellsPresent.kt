package com.sortinghat.pattern_detector.api

import com.sortinghat.pattern_detector.domain.model.smells.CyclicDependenciesSmell
import com.sortinghat.pattern_detector.domain.model.smells.SharedPersistenceSmell
import kotlinx.serialization.Serializable

@Serializable
data class SmellsPresent(
    val sharedPersistenceSmell: Set<SharedPersistenceSmell>,
    val cyclicDependenciesSmell: Set<CyclicDependenciesSmell>
)
