package com.sortinghat.pattern_detector.db

import com.sortinghat.pattern_detector.db.tables.Systems
import com.sortinghat.pattern_detector.domain.System
import com.sortinghat.pattern_detector.domain.SystemRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class SystemRepositoryImpl(private val db: Database) : SystemRepository {
	override fun save(system: System): System = when (system.id) {
		null -> store(system)
		else -> update(system)
	}

	private fun update(system: System): System {
		transaction(db) {
			Systems.update({ Systems.id eq system.id!! }) {
				it[name] = system.name
			}
		}

		return system
	}

	private fun store(system: System): System {
		val id = transaction(db) {
			Systems.insertAndGetId {
				it[name] = system.name
			}
		}

		return system.copy(id = id.value)
	}
}