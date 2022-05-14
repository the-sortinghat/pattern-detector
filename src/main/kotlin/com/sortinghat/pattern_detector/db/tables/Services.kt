package com.sortinghat.pattern_detector.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object Services : IntIdTable() {
    val name = varchar("name", 255)
    val systemName = varchar("system_name", 255)
    val moduleId = reference("module_id", Modules)

    init {
        uniqueIndex(name, systemName)
    }
}