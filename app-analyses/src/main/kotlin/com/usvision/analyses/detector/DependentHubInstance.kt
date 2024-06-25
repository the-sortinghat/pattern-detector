package com.usvision.analyses.detector

import com.usvision.model.domain.Microservice
import kotlinx.serialization.Serializable

@Serializable
data class DependentHubInstance(
    val microservice: Microservice,
    val numberOfDependencies: Int
) : ArchitectureInsight