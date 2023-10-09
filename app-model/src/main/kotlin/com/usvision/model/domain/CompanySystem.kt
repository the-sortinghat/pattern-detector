package com.usvision.model.domain

import com.usvision.model.systemcomposite.SystemOfSystems
import com.usvision.model.visitor.Visitor

data class CompanySystem(
    override val name: String
) : SystemOfSystems() {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
        subsystems.forEach { it.accept(visitor) }
    }
}