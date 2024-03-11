package com.usvision.analyses.detector

import com.usvision.analyses.analyzer.Count
import com.usvision.analyses.analyzer.DatabaseUsage
import com.usvision.analyses.analyzer.NumberOfClients
import com.usvision.analyses.analyzer.Relationship
import com.usvision.model.domain.Microservice
import com.usvision.model.domain.databases.PostgreSQL
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SharedPersistenceTest {

    private lateinit var underTest: SharedPersistence

    @MockK
    private lateinit var mockNclients: NumberOfClients

    @MockK
    private lateinit var mockDbUsages: DatabaseUsage

    @BeforeTest
    fun `create clean, new instance of SharedPersistence`() {
        MockKAnnotations.init(this)
        underTest = SharedPersistence(
            nClients = mockNclients,
            dbUsage = mockDbUsages
        )
    }

    @Test
    fun `it identifies one when given a shared database by two services`() {
        // given
        val msOne = Microservice(name = "one")
        val msTwo = Microservice(name = "two")
        val sharedDb = PostgreSQL(id = "id")

        every { mockNclients.getResults() } returns mapOf(
            sharedDb to Count(value = 2, type = "Int", unit = "clients")
        )
        every { mockDbUsages.getResults() } returns mapOf(
            sharedDb to setOf(
                Relationship(with = msOne),
                Relationship(with = msTwo)
            )
        )

        // when
        underTest.run()
        val results = underTest.getInstances()

        // then
        assertEquals(results.size, 1)
        assertIs<SharedPersistenceInstance>(results.first())
    }

    @Test
    fun `it doesnt identify any when given an exclusive database`() {
        // given
        val ms = Microservice(name = "one")
        val db = PostgreSQL(id = "id")

        every { mockNclients.getResults() } returns mapOf(
            db to Count(value = 1, type = "Int", unit = "clients")
        )
        every { mockDbUsages.getResults() } returns mapOf(
            db to setOf(Relationship(with = ms))
        )

        // when
        underTest.run()
        val results = underTest.getInstances()

        // then
        assertEquals(results.size, 0)
    }
}