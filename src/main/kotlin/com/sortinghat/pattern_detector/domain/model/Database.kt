package com.sortinghat.pattern_detector.domain.model

import com.sortinghat.pattern_detector.domain.behaviors.Measurable
import com.sortinghat.pattern_detector.domain.behaviors.Visitable
import com.sortinghat.pattern_detector.domain.behaviors.Visitor

@Suppress("unused")
enum class DataSource {
    MySql, PostgreSql, MariaDb,
    CassandraDb, MongoDb, Redis,
    SqlServer, Oracle, Neo4j,
    PlainText, Relational, Document,
    Graph, Column, KeyValue
}

data class Database(
    val name: String,
    val type: DataSource,
    val usages: MutableSet<DatabaseUsage> = mutableSetOf(),
    val bag: MetricBag = MetricBag()
) : Visitable, Measurable by bag {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    override fun children(): Iterable<Visitable> {
        return usages
    }

    fun addUsage(usage: DatabaseUsage) {
        this.usages.add(usage)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Database) return false

        if (name != other.name) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }

    override fun toString(): String {
        return "Database(name='$name', type=$type)"
    }


}