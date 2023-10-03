package com.usvision.model

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class DomainTest {
    @Test
    fun `ensure two systems with same name are the same`() {
        // given
        val commonName = "common"
        val sys1 = CompanySystem(name = commonName)
        val sys2 = CompanySystem(name = commonName)

        // when
        val result = sys1 == sys2

        // then
        assertTrue { result }
    }

    @Test
    fun `ensure two systems with different names are not the same`() {
        // given
        val sys1 = CompanySystem(name = "sys1")
        val sys2 = CompanySystem(name = "sys2")

        // when
        val result = sys1 == sys2

        // then
        assertFalse { result }
    }

    @Test
    fun `ensure two microservices with same name are the same`() {
        // given
        val commonName = "common"
        val ms1 = Microservice(name = commonName)
        val ms2 = Microservice(name = commonName)

        // when
        val result = ms1 == ms2

        // then
        assertTrue { result }
    }

    @Test
    fun `ensure two microservices with different names are not the same`() {
        // given
        val ms1 = CompanySystem(name = "ms1")
        val ms2 = CompanySystem(name = "ms2")

        // when
        val result = ms1 == ms2

        // then
        assertFalse { result }
    }

    @Test
    fun `ensure a system and a microservice with same name are not the same`() {
        // given
        val commonName = "common"
        val sys: System = CompanySystem(name = commonName)
        val ms: System = Microservice(name = commonName)

        // when
        val result = sys == ms

        // then
        assertFalse { result }
    }

    @Test
    fun `ensure two databases are the same when they have the same id`() {
        // given
        val id = "any-id"
        val db1 = PostgreSQL(id = id, description = "whatever")
        val db2 = PostgreSQL(id = id, description = "other whatever")

        // when
        val result = db1 == db2

        // then
        assertTrue { result }
    }

    @Test
    fun `ensure two databases are the not same when they have the same attributes, but different ids`() {
        // given
        val commonDescription = "bla bla bla"
        val db1 = PostgreSQL(id = "one", description = commonDescription)
        val db2 = PostgreSQL(id = "two", description = commonDescription)

        // when
        val result = db1 == db2

        // then
        assertFalse { result }
    }

    @Test
    fun `ensure two databases without id are equal when their attributes are equal`() {
        // given
        val commonDescription = "foo"
        val idlessDb1 = PostgreSQL(description = commonDescription)
        val idlessDb2 = PostgreSQL(description = commonDescription)

        // when
        val result = idlessDb1 == idlessDb2

        // then
        assertTrue { result }
    }

    @Test
    fun `ensure two databases without id are different when their attributes are different`() {
        // given
        val idlessDb1 = PostgreSQL(description = "foo")
        val idlessDb2 = PostgreSQL(description = "bar")

        // when
        val result = idlessDb1 == idlessDb2

        // then
        assertFalse { result }
    }

    @Test
    fun `ensure two channels are the same when they have the same id`() {
        // given
        val id = "any-id"
        val mc1 = MessageChannel(id = id, name = "whatever")
        val mc2 = MessageChannel(id = id, name = "other whatever")

        // when
        val result = mc1 == mc2

        // then
        assertTrue { result }
    }

    @Test
    fun `ensure two channels are the not same when they have the same attributes, but different ids`() {
        // given
        val commonName = "bla bla bla"
        val mc1 = MessageChannel(id = "one", name = commonName)
        val mc2 = MessageChannel(id = "two", name = commonName)

        // when
        val result = mc1 == mc2

        // then
        assertFalse { result }
    }

    @Test
    fun `ensure two channels without id are equal when their attributes are equal`() {
        // given
        val commonName = "foo"
        val idlessMc1 = MessageChannel(name = commonName)
        val idlessMc2 = MessageChannel(name = commonName)

        // when
        val result = idlessMc1 == idlessMc2

        // then
        assertTrue { result }
    }

    @Test
    fun `ensure two channels without id are different when their attributes are different`() {
        // given
        val idlessMc1 = MessageChannel(name = "foo")
        val idlessMc2 = MessageChannel(name = "bar")

        // when
        val result = idlessMc1 == idlessMc2

        // then
        assertFalse { result }
    }
}