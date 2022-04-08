package com.sortinghat.pattern_detector.db

import com.sortinghat.pattern_detector.db.tables.Systems
import com.sortinghat.pattern_detector.domain.System
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import java.util.UUID
import java.lang.IllegalArgumentException
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
		val system = System.create("test")
		val unknownUUID = system.id
		deleteByUUID(unknownUUID)
		val countBefore = getCount()

		// when
		underTest.save(system)
		val countAfter = getCount()

		// then
		assertEquals(countAfter - countBefore, 1L)
	}

	@Test
	fun `save updates an existing record when given a system with a known UUID`() {
		// given
		val knownID = UUID.randomUUID()
		val existingSystem = System.hydrate("test", knownID)
		ensureExists(existingSystem)

		// when
		val updateViewOfExistingSystem = System.hydrate("new name", knownID)
		underTest.save(updateViewOfExistingSystem)

		// then
		val systemFound = getByUUID(knownID)[0]
		assertEquals(systemFound.id, knownID)
		assertEquals(systemFound.name, updateViewOfExistingSystem.name)
	}

	@Test
	fun `save throws IllegalArgumentException when given a new system with duplicated name`() {
		// given
		val duplicatedName = "dup name"
		ensureExists(System.create(duplicatedName))

		// when & then
		assertThrows<IllegalArgumentException> {
			underTest.save(System.create(duplicatedName))
		}
	}

	@Test
	fun `save throws IllegalArgumentException when given an update to a duplicated name`() {
		// given
		val duplicatedName = "dup name"
		val existingSystem = System.create(duplicatedName)
		ensureExists(existingSystem)
		val secondSystem = System.create("different name")
		ensureExists(secondSystem)

		// when & then
		val updateViewOfSecondSystem = secondSystem.copy(name = duplicatedName)
		assertThrows<IllegalArgumentException> {
			underTest.save(updateViewOfSecondSystem)
		}
	}


	private fun getCount() = transaction {
		Systems.selectAll().count()
	}

	private fun getByUUID(id: UUID) = transaction {
		Systems
			.select { Systems.uuid eq id.toString() }
				.limit(1)
				.map {
					System(
							name = it[Systems.name],
							id = UUID.fromString(it[Systems.uuid])
					)
				}
	}

	private fun deleteByUUID(uuid: UUID) = transaction {
		Systems.deleteWhere { Systems.uuid eq uuid.toString() }
	}

	private fun ensureExists(system: System) = transaction {
		Systems.insert {
			it[uuid] = system.id.toString()
			it[name] = system.name
		}
	}
}