package com.sortinghat.pattern_detector.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object DatabaseUsages : IntIdTable() {
    val serviceId = reference("service_id", Services)
    val databaseId = reference("database_id", Databases)
    val accessMode = varchar("access_mode", 255)

    init {
        uniqueIndex(serviceId, databaseId)
    }
}