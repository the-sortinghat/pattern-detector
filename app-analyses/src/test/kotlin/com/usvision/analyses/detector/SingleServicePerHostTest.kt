package com.usvision.analyses.detector

import com.usvision.analyses.analyzer.*
import com.usvision.model.domain.Microservice
import com.usvision.model.domain.Module
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlin.test.*

internal class SingleServicePerHostTest {
    private lateinit var underTest: SingleServicePerHost

    @MockK
    private lateinit var mockMicroservicesOfModule: MicroservicesOfModule

    @MockK
    private lateinit var mockNumberOfMicroservicesInAHost: NumberOfMicroservicesInAHost

    @MockK
    private lateinit var mockNops: NumberOfExposedOperations

    @BeforeTest
    fun `create clean, new instance of SingleServicePerHost`() {
        MockKAnnotations.init(this)
        underTest = SingleServicePerHost(
            coHostedMsv = mockMicroservicesOfModule,
            nMsvPerHost = mockNumberOfMicroservicesInAHost,
            nops = mockNops
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
        every { mockNops.getResults() } returns mapOf(
            microservice to Count(
                value = SingleServicePerHost.MAX_COHESIVE_THRESHOLD - 1,
                type = "Int",
                unit = "operations"
            )
        )

        // when
        underTest.run()
        val results = underTest.getInstances()

        // then
        assertEquals(results.size, 1)
        assertIs<SingleServicePerHostInstance>(results.first())
    }

    @Test
    fun `it does not detect when given a module with a non-cohesive single microservice`() {
        // given
        val module = Module.createWithId()
        val microservice = Microservice(name = "foo")
        every { mockMicroservicesOfModule.getResults() } returns mapOf(
            module to setOf(Relationship(with = microservice))
        )
        every { mockNumberOfMicroservicesInAHost.getResults() } returns mapOf(
            module to Count(value = 1, type = "Int", unit = "microservices")
        )
        every { mockNops.getResults() } returns mapOf(
            microservice to Count(
                value = SingleServicePerHost.MAX_COHESIVE_THRESHOLD + 1,
                type = "Int",
                unit = "operations"
            )
        )

        // when
        underTest.run()
        val results = underTest.getInstances()

        // then
        assertTrue { results.isEmpty() }
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
        every { mockNops.getResults() } returns mapOf(
            foo to Count(
                value = SingleServicePerHost.MAX_COHESIVE_THRESHOLD - 1,
                type = "Int",
                unit = "operations"
            ),
            baz to Count(
                value = SingleServicePerHost.MAX_COHESIVE_THRESHOLD - 1,
                type = "Int",
                unit = "operations"
            )
        )

        // when
        underTest.run()
        val results = underTest.getInstances()

        // then
        assertEquals(results.size, 0)
    }
}