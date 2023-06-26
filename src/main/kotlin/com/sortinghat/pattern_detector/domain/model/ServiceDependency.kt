package com.sortinghat.pattern_detector.domain.model

import com.sortinghat.pattern_detector.domain.behaviors.Visitable
import com.sortinghat.pattern_detector.domain.behaviors.Visitor

data class ServiceDependency(
    val serviceDepId: Service
) : Visitable {
    init{
        serviceDepId.addDepend(this)
    }

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    override fun children(): Iterable<Visitable> {
        return listOf(serviceDepId)
    }

    override fun toString(): String {
        return "ServiceDependency(serviceDepId='$serviceDepId')"
    }

}
