package com.sortinghat.pattern_detector.db

import com.sortinghat.pattern_detector.db.tables.Systems
import com.sortinghat.pattern_detector.domain.System
import com.sortinghat.pattern_detector.domain.SystemRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class SystemRepositoryImpl(private val db: Database) : SystemRepository {
	override fun save(system: System): System {
		transaction(db) {
			Systems.insert {
				it[uuid] = system.id.toString()
				it[name] = system.name
			}
		}

		return system
	}

}