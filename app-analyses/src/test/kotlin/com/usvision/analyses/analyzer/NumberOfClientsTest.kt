package com.usvision.analyses.analyzer

import com.usvision.model.domain.CompanySystem
import com.usvision.model.domain.Microservice
import com.usvision.model.domain.databases.PostgreSQL
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NumberOfClientsTest {

    private lateinit var underTest: NumberOfClients

    @BeforeEach
    fun `create a clean, new instance of NumberOfClients`() {
        underTest = NumberOfClients()
    }

    @Test
    fun `it returns 1 for a single client`() {
        // given
        val ms = Microservice(name = "test")
        val db = PostgreSQL(id = "db")
        ms.addDatabaseConnection(db)

        // when
        ms.accept(underTest)
        val results = underTest.getResults()

        // then
        assertTrue { db in results }
        assertEquals(results[db]!!.value, 1)
    }

    @Test
    fun `it returns 2 when used by two microservice of the same company`() {
        // given
        val sys = CompanySystem(name = "context")
        val ms1 = Microservice(name = "one")
        val ms2 = Microservice(name = "two")
        val db = PostgreSQL(id = "unique")
        ms1.addDatabaseConnection(db)
        ms2.addDatabaseConnection(db)
        sys.addSubsystem(ms1)
        sys.addSubsystem(ms2)

        // when
        sys.accept(underTest)
        val results = underTest.getResults()

        // then
        assertTrue { db in results }
        assertEquals(results[db]!!.value, 2)
    }

    @Test
    fun `it returns 2 when used by two microservice of the same company that has 3 microservices`() {
        // given
        val sys = CompanySystem(name = "context")
        val ms1 = Microservice(name = "one")
        val ms2 = Microservice(name = "two")
        val ms3 = Microservice(name = "three")
        val db = PostgreSQL(id = "unique")
        ms1.addDatabaseConnection(db)
        ms2.addDatabaseConnection(db)
        sys.addSubsystem(ms1)
        sys.addSubsystem(ms2)
        sys.addSubsystem(ms3)

        // when
        sys.accept(underTest)
        val results = underTest.getResults()

        // then
        assertTrue { db in results }
        assertEquals(results[db]!!.value, 2)
    }
}