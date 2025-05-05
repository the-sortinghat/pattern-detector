package com.usvision.model.domain

import com.usvision.model.visitor.Visitable
import com.usvision.model.visitor.Visitor
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Module(
    val id: String
) : Visitable {
    companion object {
        fun createWithId() = Module(id = UUID.randomUUID().toString())
    }

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}