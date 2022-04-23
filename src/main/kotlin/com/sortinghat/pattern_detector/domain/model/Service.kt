package com.sortinghat.pattern_detector.domain.model

import com.sortinghat.pattern_detector.domain.behaviors.Visitable
import com.sortinghat.pattern_detector.domain.behaviors.Visitor

data class Service(
    val name: String,
    val systemName: Slug,
    val usages: MutableSet<DatabaseUsage> = mutableSetOf(),
    val operations: MutableSet<Operation> = mutableSetOf(),
) : Visitable {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    fun addUsage(usage: DatabaseUsage) {
        this.usages.add(usage)
    }

    fun addOperation(operation: Operation) {
        this.operations.add(operation)
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