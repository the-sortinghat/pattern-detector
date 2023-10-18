package com.usvision.analyses.analyzer

import com.usvision.model.domain.CompanySystem
import com.usvision.model.domain.Microservice
import com.usvision.model.domain.operations.RestEndpoint
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class NumberOfWritingExposedOperationsTest {
    private lateinit var underTest: NumberOfWritingExposedOperations

    @BeforeTest
    fun `create clean, new instance of NumberOfExposedOperations`() {
        underTest = NumberOfWritingExposedOperations()
    }

    @Test
    fun `it gets 0 from a noop microservice M`() {
        // given
        val M = Microservice(name = "noop")

        // when
        M.accept(underTest)
        val results = underTest.getResults()

        // then
        assertTrue { M in results }
        assertEquals(results[M]!!.value, 0)
    }

    @Test
    fun `it gets 0 from a CompSys S with a noop microservice M`() {
        // given
        val S = CompanySystem(name = "company")
        val M = Microservice(name = "noop")
        S.addSubsystem(M)

        // when
        S.accept(underTest)
        val results = underTest.getResults()

        // then
        assertTrue { S in results }
        assertEquals(results[S]!!.value, 0)
    }

    @Test
    fun `it gets 0 from a microservice M with a single reading operation`() {
        // given
        val M = Microservice(name = "noop")
        val op = RestEndpoint("GET", "/hello")
        M.exposeOperation(op)

        // when
        M.accept(underTest)
        val results = underTest.getResults()

        // then
        assertTrue { M in results }
        assertEquals(results[M]!!.value, 0)
    }

    @Test
    fun `it gets 0 from a CompSys S with a single reading operation microservice`() {
        // given
        val S = CompanySystem(name = "company")
        val M = Microservice(name = "noop")
        val op = RestEndpoint("GET", "/hello")
        M.exposeOperation(op)
        S.addSubsystem(M)

        // when
        S.accept(underTest)
        val results = underTest.getResults()

        // then
        assertTrue { S in results }
        assertEquals(results[S]!!.value, 0)
    }

    @Test
    fun `it gets 1 from a microservice M with a single writing operation`() {
        // given
        val M = Microservice(name = "noop")
        val op = RestEndpoint("POST", "/hello")
        M.exposeOperation(op)

        // when
        M.accept(underTest)
        val results = underTest.getResults()

        // then
        assertTrue { M in results }
        assertEquals(results[M]!!.value, 1)
    }

    @Test
    fun `it gets 1 from a CompSys S with a single writing operation microservice`() {
        // given
        val S = CompanySystem(name = "company")
        val M = Microservice(name = "noop")
        val op = RestEndpoint("POST", "/hello")
        M.exposeOperation(op)
        S.addSubsystem(M)

        // when
        S.accept(underTest)
        val results = underTest.getResults()

        // then
        assertTrue { S in results }
        assertEquals(results[S]!!.value, 1)
    }
}