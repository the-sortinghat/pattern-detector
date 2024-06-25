package com.usvision.analyses.detector

import com.usvision.model.domain.Microservice
import kotlinx.serialization.Serializable

@Serializable
data class CentralizedServiceInstance(
    val microservice: Microservice,
    val numberOfDependentServices: Int
) : ArchitectureInsight