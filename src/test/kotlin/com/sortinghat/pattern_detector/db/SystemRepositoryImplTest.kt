package com.sortinghat.pattern_detector.db

import com.sortinghat.pattern_detector.db.tables.Systems
import com.sortinghat.pattern_detector.domain.System
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.UUID
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

		underTest = SystemRepositoryImpl(testDB)
	}

	@AfterEach
	fun clearDB() {
		transaction {
			Systems.deleteAll()
		}
	}

	@Test
	fun `save adds a new record when given a system with unknown UUID`() {
		// given
		val unknownUUID = UUID.randomUUID()
		deleteByUUID(unknownUUID)
		val system = System("test", unknownUUID)
		val countBefore = getCount()

		// when
		underTest.save(system)
		val countAfter = getCount()

		// then
		assertEquals(countAfter - countBefore, 1L)
	}

	private fun getCount() = transaction {
		Systems.selectAll().count()
	}

	private fun deleteByUUID(uuid: UUID) = transaction {
		Systems.deleteWhere { Systems.uuid eq uuid.toString() }
	}
}