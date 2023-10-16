package com.usvision.model.domain.operations

import kotlinx.serialization.Serializable

@Serializable
data class RestEndpoint(
    val httpVerb: String,
    val path: String,
    val description: String = ""
) : Operation