package com.usvision.analyses.analyzer

import com.usvision.model.domain.Microservice
import com.usvision.model.domain.MessageChannel
import com.usvision.model.domain.databases.PostgreSQL
import com.usvision.model.domain.operations.RestEndpoint
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class NumberOfDependenciesTest {
    private lateinit var underTest: NumberOfDependencies

    @BeforeTest
    fun `create clean, new instance of NumberOfDependencies`() {
        underTest = NumberOfDependencies()
    }

    @Test
    fun `it gets 0 from a microservice with no dependencies`() {
        // given
        val ms = Microservice(name = "noop")

        // when
        ms.accept(underTest)
        val results = underTest.getResults()

        // then
        assertTrue { ms in results }
        assertEquals(results[ms]!!.value, 0)
    }

    @Test
    fun `it gets 1 from a microservice with a single operation dependency`() {
        // given
        val ms = Microservice(name = "noop")
        val op = RestEndpoint("GET", "/hello")
        ms.consumeOperation(op)

        // when
        ms.accept(underTest)
        val results = underTest.getResults()

        // then
        assertTrue { ms in results }
        assertEquals(results[ms]!!.value, 1)
    }

    @Test
    fun `it gets 1 from a microservice with a single channel dependency`() {
        // given
        val ms = Microservice(name = "noop")
        val channel = MessageChannel(name = "topic", id = "1234-abcd")
        ms.addSubscribedChannel(channel)

        // when
        ms.accept(underTest)
        val results = underTest.getResults()

        // then
        assertTrue { ms in results }
        assertEquals(results[ms]!!.value, 1)
    }

    @Test
    fun `it gets 1 from a microservice with a single database dependency`() {
        // given
        val ms = Microservice(name = "noop")
        val db = PostgreSQL(id = "db")
        ms.addDatabaseConnection(db)

        // when
        ms.accept(underTest)
        val results = underTest.getResults()

        // then
        assertTrue { ms in results }
        assertEquals(results[ms]!!.value, 1)
    }

    @Test
    fun `it gets 3 from a microservice with one of each type of dependency`() {
        // given
        val ms = Microservice(name = "noop")
        val op = RestEndpoint("GET", "/hello")
        ms.consumeOperation(op)
        val channel = MessageChannel(name = "topic", id = "1234-abcd")
        ms.addSubscribedChannel(channel)
        val db = PostgreSQL(id = "db")
        ms.addDatabaseConnection(db)

        // when
        ms.accept(underTest)
        val results = underTest.getResults()

        // then
        assertTrue { ms in results }
        assertEquals(results[ms]!!.value, 3)
    }
}