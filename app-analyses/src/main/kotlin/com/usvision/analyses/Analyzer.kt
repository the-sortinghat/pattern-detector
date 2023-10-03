package com.usvision.analyses

import com.usvision.model.Visitable
import com.usvision.model.Visitor

interface Analyzer<out T> : Visitor {
    fun getResults(): Map<Visitable, T>
}

data class Relationship(val with: Visitable)

interface RelationshipAnalyzer : Analyzer<Relationship>

