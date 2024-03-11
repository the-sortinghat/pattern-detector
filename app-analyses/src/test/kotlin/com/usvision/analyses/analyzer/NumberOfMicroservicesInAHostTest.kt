package com.usvision.analyses.analyzer

import com.usvision.model.domain.CompanySystem
import com.usvision.model.domain.Microservice
import com.usvision.model.domain.Module
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

internal class NumberOfMicroservicesInAHostTest {
    private lateinit var underTest: NumberOfMicroservicesInAHost

    @BeforeTest
    fun `create clean, new instance of NumberOfMicroservicesInAHost`() {
        underTest = NumberOfMicroservicesInAHost()
    }

    @Test
    fun `it counts 1 when there is only one microservice`() {
        // given
        val module = Module.createWithId()
        val system = Microservice(name = "foo").also { it.module = module }

        // when
        system.accept(underTest)
        val results = underTest.getResults()

        // then
        assertContains(results, module)
        assertEquals(results[module]!!.value, 1)
    }

    @Test
    fun `it counts 2 when there are two microservice in the same module`() {
        // given
        val module = Module.createWithId()
        val foo = Microservice(name = "foo").also { it.module = module }
        val baz = Microservice(name = "baz").also { it.module = module }
        val system = CompanySystem(name = "test")
        system.addSubsystem(foo)
        system.addSubsystem(baz)

        // when
        system.accept(underTest)
        val results = underTest.getResults()

        // then
        assertContains(results, module)
        assertEquals(results[module]!!.value, 2)
    }
}