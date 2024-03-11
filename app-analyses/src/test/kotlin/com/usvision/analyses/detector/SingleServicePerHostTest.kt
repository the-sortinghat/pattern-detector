package com.usvision.analyses.detector

import com.usvision.analyses.analyzer.Count
import com.usvision.analyses.analyzer.MicroservicesOfModule
import com.usvision.analyses.analyzer.NumberOfMicroservicesInAHost
import com.usvision.analyses.analyzer.Relationship
import com.usvision.model.domain.Microservice
import com.usvision.model.domain.Module
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

internal class SingleServicePerHostTest {
    private lateinit var underTest: SingleServicePerHost

    @MockK
    private lateinit var mockMicroservicesOfModule: MicroservicesOfModule

    @MockK
    private lateinit var mockNumberOfMicroservicesInAHost: NumberOfMicroservicesInAHost

    @BeforeTest
    fun `create clean, new instance of SingleServicePerHost`() {
        MockKAnnotations.init(this)
        underTest = SingleServicePerHost(
            coHostedMsv = mockMicroservicesOfModule,
            nMsvPerHost = mockNumberOfMicroservicesInAHost
        )
    }

    @Test
    fun `it detects when given a module with a single microservice`() {
        // given
        val module = Module.createWithId()
        val microservice = Microservice(name = "foo")
        every { mockMicroservicesOfModule.getResults() } returns mapOf(
            module to setOf(Relationship(with = microservice))
        )
        every { mockNumberOfMicroservicesInAHost.getResults() } returns mapOf(
            module to Count(value = 1, type = "Int", unit = "microservices")
        )

        // when
        underTest.run()
        val results = underTest.getInstances()

        // then
        assertEquals(results.size, 1)
        assertIs<SingleServicePerHostInstance>(results.first())
    }

    @Test
    fun `it detects no instance when all modules are shared`() {
        // given
        val module = Module.createWithId()
        val foo = Microservice(name = "foo")
        val baz = Microservice(name = "baz")
        every { mockMicroservicesOfModule.getResults() } returns mapOf(
            module to setOf(Relationship(with = foo), Relationship(with = baz))
        )
        every { mockNumberOfMicroservicesInAHost.getResults() } returns mapOf(
            module to Count(value = 2, type = "Int", unit = "microservices")
        )

        // when
        underTest.run()
        val results = underTest.getInstances()

        // then
        assertEquals(results.size, 0)
    }
}