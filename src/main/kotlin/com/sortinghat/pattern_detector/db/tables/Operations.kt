package com.sortinghat.pattern_detector.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object Operations : IntIdTable() {
    val verb = varchar("verb", 255)
    val uri = varchar("uri", 255)
    val exposerId = reference("exposer_id", Services)

    init {
        uniqueIndex(exposerId, verb, uri)
    }
}