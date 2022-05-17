package com.sortinghat.pattern_detector.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object MessageChannels : IntIdTable() {
    val name = varchar("name", 255)
}