package com.usvision.analyses.detector

import com.usvision.analyses.analyzer.*
import com.usvision.model.domain.Microservice
import com.usvision.model.domain.databases.PostgreSQL
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlin.test.*


class DatabasePerServiceTest {

    private lateinit var underTest: DatabasePerService

    @MockK
    private lateinit var mockNops: NumberOfExposedOperations
    @MockK
    private lateinit var mockNclients: NumberOfClients
    @MockK
    private lateinit var mockDbUsages: DatabaseUsage

    @BeforeTest
    fun `create clean, new instance of DatabasePerService`() {
        MockKAnnotations.init(this)
        underTest = DatabasePerService(
            nops = mockNops,
            nclients = mockNclients,
            dbUsage = mockDbUsages
        )
    }

    @Test
    fun `it identifies one when given a cohesive ms being the single user of a db`() {
        // given
        val ms = Microservice(name = "mock")
        val db = PostgreSQL(id = "id")

        every { mockNops.getResults() } returns mapOf(
            ms to Count(value = 1, type = "Int", unit = "operations")
        )
        every { mockNclients.getResults() } returns mapOf(
            db to Count(value = 1, type = "Int", unit = "clients")
        )
        every { mockDbUsages.getResults() } returns mapOf(
            db to setOf(Relationship(with = ms)))

        // when
        underTest.run()
        val results = underTest.getInstances()

        // then
        assertEquals(results.size, 1)
        assertIs<DatabasePerServiceInstance>(results.first())
    }

    @Test
    fun `it does not identify when ms isn't cohesive but is the single user of a db`() {
        // given
        val ms = Microservice(name = "mock")
        val db = PostgreSQL(id = "id")

        every { mockNops.getResults() } returns mapOf(
            ms to Count(value = DatabasePerService.MAX_COHESIVE_THRESHOLD + 1, type = "Int", unit = "operations")
        )
        every { mockNclients.getResults() } returns mapOf(
            db to Count(value = 1, type = "Int", unit = "clients")
        )
        every { mockDbUsages.getResults() } returns mapOf(
            db to setOf(Relationship(with = ms)))

        // when
        underTest.run()
        val results = underTest.getInstances()

        // then
        assertEquals(results.size, 0)
    }

    @Test
    fun `it does not identify when given a cohesive ms uses a shared db`() {
        // given
        val ms = Microservice(name = "mock")
        val db = PostgreSQL(id = "id")

        every { mockNops.getResults() } returns mapOf(
            ms to Count(value = 1, type = "Int", unit = "operations")
        )
        every { mockNclients.getResults() } returns mapOf(
            db to Count(value = DatabasePerService.MAX_CLIENTS_THRESHOLD + 1, type = "Int", unit = "clients")
        )
        every { mockDbUsages.getResults() } returns mapOf(
            db to setOf(Relationship(with = ms)))

        // when
        underTest.run()
        val results = underTest.getInstances()

        // then
        assertEquals(results.size, 0)
    }
}