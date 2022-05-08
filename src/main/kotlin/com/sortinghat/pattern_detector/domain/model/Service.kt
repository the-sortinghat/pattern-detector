package com.sortinghat.pattern_detector.domain.model

import com.sortinghat.pattern_detector.domain.behaviors.Measurable
import com.sortinghat.pattern_detector.domain.behaviors.Visitable
import com.sortinghat.pattern_detector.domain.behaviors.Visitor

data class Service(
    val name: String,
    val systemName: Slug,
    val usages: MutableSet<DatabaseUsage> = mutableSetOf(),
    val exposedOperations: MutableSet<Operation> = mutableSetOf(),
    val consumedOperations: MutableSet<Operation> = mutableSetOf(),
    val bag: MetricBag = MetricBag(),
    val module: Module = Module(),
) : Visitable, Measurable by bag {

    init {
        module.addService(this)
    }

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    override fun children(): Iterable<Visitable> {
        return usages + exposedOperations + module
    }

    fun addUsage(usage: DatabaseUsage) {
        this.usages.add(usage)
    }

    fun expose(operation: Operation) {
        this.exposedOperations.add(operation)
    }
    fun consume(operation: Operation) {
        this.consumedOperations.add(operation)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Service) return false

        if (name != other.name) return false
        if (systemName != other.systemName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + systemName.hashCode()
        return result
    }


}