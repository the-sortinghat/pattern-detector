package com.usvision.model.domain.databases

import com.usvision.model.visitor.Visitable
import kotlinx.serialization.Serializable

@Serializable
sealed interface Database : Visitable {
    val id: String?
    val description: String
}