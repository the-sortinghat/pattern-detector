package com.sortinghat.pattern_detector.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object Systems : IntIdTable() {
	val name = varchar("name", 255).uniqueIndex()
}