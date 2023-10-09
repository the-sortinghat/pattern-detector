package com.usvision.model.domain.databases

import com.usvision.model.visitor.Visitor

data class PostgreSQL(
    override val description: String = "PostgreSQL database",
    override val id: String? = null
) : Database {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    override fun equals(other: Any?): Boolean {
        return if (other !is PostgreSQL) false
        else if (id == null || other.id == null) description == other.description
        else id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}