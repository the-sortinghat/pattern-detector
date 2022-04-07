package com.sortinghat.pattern_detector.db

import com.sortinghat.pattern_detector.db.tables.Systems
import com.sortinghat.pattern_detector.domain.System
import com.sortinghat.pattern_detector.domain.SystemNotFoundException
import com.sortinghat.pattern_detector.domain.SystemRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class SystemRepositoryImpl(private val db: Database) : SystemRepository {
	override fun save(system: System): System = when (system.id) {
		null -> store(system)
		else -> update(system)
	}

	override fun findById(id: Int): System {
		val systems = transaction {
			Systems
				.select { Systems.id eq id }
				.map {
					System(
						id = it[Systems.id].value,
						name = it[Systems.name]
					)
				}
		}

		if (systems.isEmpty())
			throw SystemNotFoundException(id)

		return systems[0]
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