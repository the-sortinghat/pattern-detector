package com.sortinghat.pattern_detector.domain.model

import com.sortinghat.pattern_detector.domain.behaviors.Visitable
import com.sortinghat.pattern_detector.domain.behaviors.Visitor

@Suppress("unused")
enum class DatabaseAccessMode {
    ReadOnly, WriteOnly, ReadWrite
}

data class DatabaseUsage(
    val service: Service,
    val database: Database,
    val accessMode: DatabaseAccessMode
) : Visitable {
    init {
        service.addUsage(this)
        database.addUsage(this)
    }

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    override fun children(): Iterable<Visitable> {
        return listOf(service, database)
    }

    override fun toString(): String {
        return "DatabaseUsage(service=$service, database=$database)"
    }


}
