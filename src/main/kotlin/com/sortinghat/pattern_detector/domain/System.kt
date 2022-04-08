package com.sortinghat.pattern_detector.domain

import java.util.*

data class System(
	val name: String,
	val id: UUID
) {
	companion object {
		fun hydrate(name: String, uuid: UUID): System {
			return System(name, id = uuid)
		}

		fun create(name: String): System {
			return System(name, UUID.randomUUID())
		}
	}
}
