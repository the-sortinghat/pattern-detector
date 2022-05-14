package com.sortinghat.pattern_detector.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object Databases : IntIdTable() {
    val name = varchar("name", 255)
    val type = varchar("type", 255)
}