package com.sortinghat.pattern_detector.db.tables

import org.jetbrains.exposed.sql.Table

object Systems : Table() {
	val uuid = varchar("uuid", 255)
	val name = varchar("name", 255)

	override val primaryKey = PrimaryKey(uuid, name = "PK_System")
}