package com.usvision.analyses.detector

import com.usvision.analyses.analyzer.AsyncDependenciesOfMicroservice
import com.usvision.analyses.analyzer.Relationship
import com.usvision.model.domain.Microservice
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlin.test.*

class AsyncMessagingTest {
    private lateinit var underTest: AsyncMessaging

    @MockK
    private lateinit var mockAsyncDeps: AsyncDependenciesOfMicroservice

    @BeforeTest
    fun `create clean, new instance of AsyncMessaging`() {
        MockKAnnotations.init(this)
        underTest = AsyncMessaging(mockAsyncDeps)
    }

    @Test
    fun `nothing is detected when no async dep is present`() {
        // given
        every { mockAsyncDeps.getResults() } returns emptyMap()

        // when
        val instances = underTest.getInstances()

        // then
        assertTrue { instances.isEmpty() }
    }

    @Test
    fun `a single instance is detected when there is a paired prod-cons`() {
        // given
        val prod = Microservice(name = "producer")
        val cons = Microservice(name = "consumer")
        every { mockAsyncDeps.getResults() } returns mapOf(
            cons to setOf(Relationship(with = prod))
        )

        // when
        underTest.run()
        val instances = underTest.getInstances()

        // then
        assertEquals(1, instances.size)
        val firstInstance = instances.first()
        assertIs<AsyncMessagingInstance>(firstInstance)
        assertEquals(prod, firstInstance.publisher)
        assertEquals(cons, firstInstance.subscriber)
    }

    @Test
    fun `multiple instances are detected when there one consumes from multiple`() {
        // given
        val prod1 = Microservice(name = "producer1")
        val prod2 = Microservice(name = "producer2")
        val cons = Microservice(name = "consumer")
        every { mockAsyncDeps.getResults() } returns mapOf(
            cons to setOf(
                Relationship(with = prod1),
                Relationship(with = prod2)
            )
        )

        // when
        underTest.run()
        val instances = underTest.getInstances()

        // then
        assertEquals(2, instances.size)
        assertTrue { instances.all { it is AsyncMessagingInstance } }
    }
}