package com.usvision.model.domain.operations

data class RestEndpoint(
    val httpVerb: String,
    val path: String,
    override val description: String = ""
) : Operation(description)