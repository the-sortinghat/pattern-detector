package com.usvision.analyses

import com.usvision.model.Visitable
import com.usvision.model.Visitor

interface Analyzer<out T> : Visitor {
    fun getResults(): Map<Visitable, T>
}

data class Relationship(val with: Visitable)

interface RelationshipAnalyzer : Analyzer<Relationship>
interface RelationshipsAnalyzer : Analyzer<Set<Relationship>>

interface Measure {
    val value: Any
    val type: String
    val unit: String
}

data class Count(
    override val value: Any,
    override val type: String,
    override val unit: String
): Measure

interface Measurer : Analyzer<Measure>