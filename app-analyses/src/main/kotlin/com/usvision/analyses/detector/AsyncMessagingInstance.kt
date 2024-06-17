package com.usvision.analyses.detector

import com.usvision.model.domain.Microservice
import kotlinx.serialization.Serializable

@Serializable
data class AsyncMessagingInstance(
    val publisher: Microservice,
    val subscriber: Microservice
) : ArchitectureInsight