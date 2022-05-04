package com.sortinghat.pattern_detector.domain.model

import com.sortinghat.pattern_detector.domain.behaviors.Visitable
import com.sortinghat.pattern_detector.domain.behaviors.Visitor

data class Module(
    val services: MutableSet<Service> = mutableSetOf()
) : Visitable {

    fun addService(service: Service) {
        services.add(service)
    }

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    override fun children(): Iterable<Visitable> {
        return services
    }
}
