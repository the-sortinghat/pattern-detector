package com.sortinghat.pattern_detector.db

import com.sortinghat.pattern_detector.db.tables.Systems
import com.sortinghat.pattern_detector.domain.System
import com.sortinghat.pattern_detector.domain.SystemRepository
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class SystemRepositoryImpl(private val db: Database) : SystemRepository {

	override fun save(system: System) = when(idExists(system.id)) {
		true -> update(system)
		else -> store(system)
	}

	private fun store(system: System): System {
		try {
			transaction(db) {
				Systems.insert {
					it[uuid] = system.id.toString()
					it[name] = system.name
				}
			}

			return system
		} catch (e: ExposedSQLException) {
			throw IllegalArgumentException("Cannot create System: name=${system.name} already taken")
		}
	}

	private fun update(system: System): System {
		try {
			transaction (db) {
				Systems.update({ Systems.uuid eq system.id.toString() }) {
					it[name] = system.name
				}
			}

			return system
		} catch (e: ExposedSQLException) {
			throw IllegalArgumentException("Cannot update System id=${system.id}: name=${system.name} already taken")
		}

	}

	private fun idExists(id: UUID): Boolean = transaction {
		Systems
			.select { Systems.uuid eq id.toString() }
			.count() > 0
	}

}