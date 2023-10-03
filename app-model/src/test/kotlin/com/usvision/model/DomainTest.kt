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
}