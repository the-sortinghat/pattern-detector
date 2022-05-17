package com.sortinghat.pattern_detector.db

import com.sortinghat.pattern_detector.db.tables.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
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
			SchemaUtils.create(Modules)
			SchemaUtils.create(Databases)
			SchemaUtils.create(Services)
			SchemaUtils.create(DatabaseUsages)
			SchemaUtils.create(Operations)
			SchemaUtils.create(ConsumedOperations)
			SchemaUtils.create(MessageChannels)
			SchemaUtils.create(Publications)
			SchemaUtils.create(Subscriptions)
		}
	}
}