package com.sortinghat.pattern_detector.db

import com.sortinghat.pattern_detector.db.tables.Services
import com.sortinghat.pattern_detector.db.tables.Systems
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseAdapterImpl (
	private val url: String,
	private val driver: String,
	private val user: String,
	private val password: String,
) : DatabaseAdapter {

	override fun connect(): Database {
		val database = Database.connect(url, driver, user, password)

		createTables()

		return database
	}

	private fun createTables() {
		transaction {
			SchemaUtils.create(Systems)
			SchemaUtils.create(Services)
		}
	}
}