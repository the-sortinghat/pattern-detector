package com.usvision.model.domain

import com.usvision.model.systemcomposite.SystemOfSystems
import com.usvision.model.visitor.Visitor
import kotlinx.serialization.Serializable

@Serializable
data class CompanySystem(
    override val name: String
) : SystemOfSystems() {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
        subsystems.forEach { it.accept(visitor) }
    }
}