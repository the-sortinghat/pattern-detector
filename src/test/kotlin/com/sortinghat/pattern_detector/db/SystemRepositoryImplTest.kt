package com.sortinghat.pattern_detector.db

import com.sortinghat.pattern_detector.db.tables.Systems
import com.sortinghat.pattern_detector.domain.SystemNotFoundException
import com.sortinghat.pattern_detector.domain.System
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
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
	fun `save stores when there's no id`() {
		val countBefore = count()

		val system = System("test")
		underTest.save(system)

		val countAfter = count()

		assertEquals(countAfter - countBefore, 1L)
	}

	@Test
	fun `save updates when there's an id`() {
		var system = System("test")
		system = underTest.save(system)

		val countBefore = count()

		val updatedSystem = system.copy(name = "tset")
		underTest.save(updatedSystem)

		val countAfter = count()

		assertEquals(countAfter - countBefore, 0L)
	}

	@Test
	fun `save throws IllegalArgumentException when creating with duplicated name`() {
		val duplicatedName = "some name to be duplicated"
		underTest.save(System(duplicatedName))

		assertThrows<IllegalArgumentException> {
			underTest.save(System(duplicatedName))
		}
	}

	@Test
	fun `save throws IllegalArgumentException when updating to a duplicated name`() {
		val duplicatedName = "some name to be duplicated"
		underTest.save(System(duplicatedName))

		var otherSystem = underTest.save(System("different name"))
		otherSystem = otherSystem.copy(name = duplicatedName)

		assertThrows<IllegalArgumentException> {
			underTest.save(otherSystem)
		}
	}

	@Test
	fun `findById throws SystemNotFound when there's no such id`() {
		assertThrows<SystemNotFoundException> {
			underTest.findById(0)
		}
	}

	@Test
	fun `findById returns a system with given id when it exists`() {
		val given = underTest.save(System("test"))

		val found = underTest.findById(given.id!!)

		assertEquals(found.id, given.id)
	}

	private fun count(): Long {
		return transaction(testDB) {
			Systems.selectAll().count()
		}
	}
}