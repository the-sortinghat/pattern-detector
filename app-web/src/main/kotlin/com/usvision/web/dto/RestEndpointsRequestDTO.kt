package com.usvision.web.dto

import com.usvision.model.domain.operations.Operation
import kotlinx.serialization.Serializable

@Serializable
data class RestEndpointsRequestDTO(
    val exposedOperations: List<Operation>,
    val consumedOperations: List<Operation>
)
