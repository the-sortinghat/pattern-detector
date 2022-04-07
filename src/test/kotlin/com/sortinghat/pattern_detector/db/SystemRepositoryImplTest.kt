package com.sortinghat.pattern_detector.db

import com.sortinghat.pattern_detector.db.tables.Systems
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class SystemRepositoryImplTest {

	lateinit var underTest: SystemRepositoryImpl

	lateinit var testDB: Database

	@BeforeAll
	fun setUp() {
		testDB = DatabaseAdapterImpl(
			url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
			driver = "org.h2.Driver",
			user = "",
			password = ""
		).connect()
	}

	@BeforeEach
	fun instantiate() {
		underTest = SystemRepositoryImpl(testDB)
	}

	@Test
	fun `save stores when there's no id`() {
		val countBefore = count()

		val system = com.sortinghat.pattern_detector.domain.System("test")
		underTest.save(system)

		val countAfter = count()

		assertEquals(countAfter - countBefore, 1L)
	}

	@Test
	fun `save updates when there's an id`() {
		var system = com.sortinghat.pattern_detector.domain.System("test")
		system = underTest.save(system)

		val countBefore = count()

		val updatedSystem = system.copy(name = "tset")
		underTest.save(updatedSystem)

		val countAfter = count()

		assertEquals(countAfter - countBefore, 0L)
	}

	private fun count(): Long {
		return transaction(testDB) {
			Systems.selectAll().count()
		}
	}
}