package com.sortinghat.pattern_detector.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

@Suppress("unused")
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

		}
	}
}