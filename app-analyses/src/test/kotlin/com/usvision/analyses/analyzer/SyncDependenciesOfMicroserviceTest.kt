package com.usvision.analyses.analyzer

import com.usvision.model.domain.CompanySystem
import com.usvision.model.domain.Microservice
import com.usvision.model.domain.operations.RestEndpoint
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import kotlin.test.BeforeTest
import kotlin.test.Test

class SyncDependenciesOfMicroserviceTest {
    private lateinit var underTest: SyncDependenciesOfMicroservice
    private lateinit var mockSync: SyncDependenciesOfMicroservice

    @BeforeTest
    fun `create clean, new instance of SyncDependenciesOfMicroservice`() {
        underTest = SyncDependenciesOfMicroservice()
        mockSync = mockk(relaxed = true)
    }

    @Test
    fun `it checks if the system is validating null pointer exception and giving as a result an empty set when there is no operation for a microservice`() {
        // given
        val sys = CompanySystem(name = "test")
        val prod = Microservice(name = "producer")
        val operation = RestEndpoint(httpVerb = "GET", path = "/data", "test")
        sys.addSubsystem(prod)
        prod.exposeOperation(operation)

        every { mockSync.getResults() } returns emptyMap()

        // when
        sys.accept(underTest)
        val results = underTest.getResults()

        // then
        assertTrue(results.isEmpty())
    }

    @Test
    fun `it connects a single consumer to a single producer`() {
        // given
        val sys = CompanySystem(name = "test")
        val prod = Microservice(name = "producer")
        val cons = Microservice(name = "consumer")
        val operation = RestEndpoint(httpVerb = "GET", path = "/data", "test")
        sys.addSubsystem(prod)
        sys.addSubsystem(cons)
        prod.exposeOperation(operation)
        cons.consumeOperation(operation)

        // when
        sys.accept(underTest)
        val results = underTest.getResults()

        // then
        assertFalse { prod in results }
        assertTrue { cons in results }
        assertEquals(1, results[cons]!!.size)
        assertTrue { Relationship(with = prod) in results[cons]!! }
    }

    @Test
    fun `an isolated consumer doesnt get computed`() {
        // given
        val cons = Microservice(name = "consumer")

        // when
        cons.accept(underTest)
        val results = underTest.getResults()

        // then
        assertFalse { cons in results }
    }
}