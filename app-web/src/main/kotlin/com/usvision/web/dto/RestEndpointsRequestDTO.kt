package com.usvision.web.dto

import com.usvision.model.domain.operations.RestEndpoint
import kotlinx.serialization.Serializable

@Serializable
data class RestEndpointsRequestDTO(
    val exposedOperations: List<RestEndpoint>,
    val consumedOperations: List<RestEndpoint>
)
