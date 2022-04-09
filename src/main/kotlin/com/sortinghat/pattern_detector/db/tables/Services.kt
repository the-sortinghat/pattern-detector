package com.sortinghat.pattern_detector.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object Services : IntIdTable() {
    val name = varchar("name", 255)
    val systemUuid = reference("system_uuid", Systems.uuid)

    init {
        uniqueIndex(systemUuid, name)
    }
}