package com.usvision.analyses.analyzer

import com.usvision.model.domain.CompanySystem
import com.usvision.model.domain.MessageChannel
import com.usvision.model.domain.databases.PostgreSQL
import com.usvision.model.domain.operations.RestEndpoint
import kotlin.test.*
import com.usvision.model.domain.Microservice as Microservice

class CoincidenceOfMicroservicesTest {
    private lateinit var underTest: CoincidenceOfMicroservices

    @BeforeTest
    fun `create clean, new instance of CoincidenceOfMicroservices`() {
        underTest = CoincidenceOfMicroservices()
    }

    @Test
    fun `it detects coincidence in sync dependencies`() {
        // given
        val sys = CompanySystem(name = "test")
        val prod = Microservice(name = "producer")
        val cons1 = Microservice(name = "consumer1")
        val cons2 = Microservice(name = "consumer2")
        val operation = RestEndpoint(httpVerb = "GET", path = "/data", "test")
        sys.addSubsystem(prod)
        sys.addSubsystem(cons1)
        sys.addSubsystem(cons2)
        prod.exposeOperation(operation)
        cons1.consumeOperation(operation)
        cons2.consumeOperation(operation)

        // when
        sys.accept(underTest)
        val results = underTest.getDetailedResults()

        // then
        assertFalse { prod in results }
        assertTrue { cons1 in results }
        assertTrue { cons2 in results }
        assertEquals(1, results[cons1]!!.size)
        assertEquals(1, results[cons2]!!.size)
        assertTrue { results[cons1]!!.any { it.second == "sync" } }
        assertTrue { results[cons2]!!.any { it.second == "sync" } }
    }

    @Test
    fun `it detects coincidence in async dependencies`() {
        // given
        val sys = CompanySystem(name = "test")
        val prod = Microservice(name = "producer")
        val cons1 = Microservice(name = "consumer1")
        val cons2 = Microservice(name = "consumer2")
        val channel = MessageChannel(name = "topic", id = "1234-abcd")
        sys.addSubsystem(prod)
        sys.addSubsystem(cons1)
        sys.addSubsystem(cons2)
        prod.addPublishChannel(channel)
        cons1.addSubscribedChannel(channel)
        cons2.addSubscribedChannel(channel)

        // when
        sys.accept(underTest)
        val results = underTest.getDetailedResults()

        // then
        assertFalse { prod in results }
        assertTrue { cons1 in results }
        assertTrue { cons2 in results }
        assertEquals(1, results[cons1]!!.size)
        assertEquals(1, results[cons2]!!.size)
        assertTrue { results[cons1]!!.any { it.second == "async" } }
        assertTrue { results[cons2]!!.any { it.second == "async" } }
    }

    @Test
    fun `it detects coincidence in database dependencies`() {
        // given
        val sys = CompanySystem(name = "test")
        val ms1 = Microservice(name = "ms1")
        val ms2 = Microservice(name = "ms2")
        val db = PostgreSQL(id = "pg")
        sys.addSubsystem(ms1)
        sys.addSubsystem(ms2)
        ms1.addDatabaseConnection(db)
        ms2.addDatabaseConnection(db)

        // when
        sys.accept(underTest)
        val results = underTest.getDetailedResults()

        // then
        assertTrue { ms1 in results }
        assertTrue { ms2 in results }
        assertEquals(1, results[ms1]!!.size)
        assertEquals(1, results[ms2]!!.size)
        assertTrue { results[ms1]!!.any { it.second == "database" } }
        assertTrue { results[ms2]!!.any { it.second == "database" } }
    }

    @Test
    fun `an isolated microservice doesn't get computed`() {
        // given
        val ms = Microservice(name = "isolated")
        ms.accept(underTest)

        // when
        val results = underTest.getDetailedResults()

        // then
        assertFalse { ms in results }
    }
}