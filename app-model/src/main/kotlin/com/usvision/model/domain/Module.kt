package com.usvision.model.domain

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Module(
    val id: String?
) {
    companion object {
        fun createWithId() = Module(id = UUID.randomUUID().toString())
    }
}