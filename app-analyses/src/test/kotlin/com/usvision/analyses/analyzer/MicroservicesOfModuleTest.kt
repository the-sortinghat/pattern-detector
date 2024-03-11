package com.usvision.analyses.analyzer

import com.usvision.model.domain.CompanySystem
import com.usvision.model.domain.Microservice
import com.usvision.model.domain.Module
import kotlin.test.*

internal class MicroservicesOfModuleTest {
    private lateinit var underTest: MicroservicesOfModule

    @BeforeTest
    fun `create clean, new instance of MicroservicesOfModule`() {
        underTest = MicroservicesOfModule()
    }

    @Test
    fun `it identifies a single-element set when there is only one microservice`() {
        // given
        val module = Module.createWithId()
        val system = Microservice(name = "foo").also { it.module = module }

        // when
        system.accept(underTest)
        val results = underTest.getResults()

        // then
        assertContains(results, module)
        assertEquals(results[module]!!.size, 1)
        assertTrue { Relationship(with = system) in results[module]!! }
    }

    @Test
    fun `it identifies a two-element set when there are two microservices`() {
        // given
        val module = Module.createWithId()
        val foo = Microservice(name = "foo").also { it.module = module }
        val baz = Microservice(name = "baz").also { it.module = module }
        val system = CompanySystem(name = "something")
        system.addSubsystem(foo)
        system.addSubsystem(baz)

        // when
        system.accept(underTest)
        val results = underTest.getResults()

        // then
        assertContains(results, module)
        assertEquals(results[module]!!.size, 2)
        assertTrue { Relationship(with = foo) in results[module]!! }
        assertTrue { Relationship(with = baz) in results[module]!! }
    }
}