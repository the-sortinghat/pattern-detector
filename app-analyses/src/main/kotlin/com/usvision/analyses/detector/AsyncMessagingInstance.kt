package com.usvision.analyses.detector

import com.usvision.model.domain.Microservice

data class AsyncMessagingInstance(
    val publisher: Microservice,
    val subscriber: Microservice
) : ArchitectureInsight