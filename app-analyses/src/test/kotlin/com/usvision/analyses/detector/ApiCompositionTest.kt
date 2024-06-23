package com.usvision.analyses.detector

import com.usvision.analyses.analyzer.*
import com.usvision.model.domain.Microservice
import com.usvision.model.visitor.Visitable
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ApiCompositionTest {
    private lateinit var underTest: ApiComposition

    @MockK
    private lateinit var mockNops: NumberOfExposedOperations

    @MockK
    private lateinit var mockNReadingOps: NumberOfReadingExposedOperations

    @MockK
    private lateinit var mockSyncDeps: SyncDependenciesOfMicroservice

    @BeforeTest
    fun `create clean, new instance of ApiComposition`() {
        MockKAnnotations.init(this)
        underTest = ApiComposition(
            nOps = mockNops,
            nReadingOps = mockNReadingOps,
            syncDeps = mockSyncDeps
        )
    }

    @Test
    fun `nothing is detected when no sync dep is present`() {
        // given
        every { mockSyncDeps.getResults() } returns emptyMap()
        every { mockNops.getResults() } returns emptyMap()
        every { mockNReadingOps.getResults() } returns emptyMap()

        // when
        underTest.run()
        val instances = underTest.getInstances()

        // then
        assertEquals(0, instances.size)
    }

    @Test
    fun `it detects for a composer of two composees`() {
        // given
        val composer = Microservice(name = "api")
        val composeeOne = Microservice(name = "one")
        val composeeTwo = Microservice(name = "two")
        every { mockSyncDeps.getResults() } returns mapOf(composer to setOf(
            Relationship(with = composeeOne),
            Relationship(with = composeeTwo)
        ))
        every { mockNops.getResults() } returns mapOf(
            composer to Count(value = 1, type = "Int", unit = "operations"),
            composeeOne to Count(value = 1, type = "Int", unit = "operations"),
            composeeTwo to Count(value = 1, type = "Int", unit = "operations")
        )
        every { mockNReadingOps.getResults() } returns mapOf(
            composer to Count(value = 1, type = "Int", unit = "operations")
        )

        // when
        underTest.run()
        val instances = underTest.getInstances()

        // then
        assertEquals(1, instances.size)
        val instance = instances.first()
        assertIs<ApiCompositionInstance>(instance)
    }

    @Test
    fun `nothing is detected for a bloated wanna-be composer of two composees`() {
        // given
        val composer = Microservice(name = "api")
        val composeeOne = Microservice(name = "one")
        val composeeTwo = Microservice(name = "two")
        every { mockSyncDeps.getResults() } returns mapOf(composer to setOf(
            Relationship(with = composeeOne),
            Relationship(with = composeeTwo)
        ))
        every { mockNops.getResults() } returns mapOf(
            composer to Count(value = ApiComposition.MAX_COHESIVE_THRESHOLD + 1, type = "Int", unit = "operations"),
            composeeOne to Count(value = 1, type = "Int", unit = "operations"),
            composeeTwo to Count(value = 1, type = "Int", unit = "operations")
        )
        every { mockNReadingOps.getResults() } returns mapOf(
            composer to Count(value = ApiComposition.MIN_READING_OPS_THRESHOLD, type = "Int", unit = "operations")
        )

        // when
        underTest.run()
        val instances = underTest.getInstances()

        // then
        assertEquals(0, instances.size)
    }

    @Test
    fun `nothing is detected for a wanna-be composer proxying a single composee`() {
        // given
        val composer = Microservice(name = "api")
        val composee = Microservice(name = "one")
        every { mockSyncDeps.getResults() } returns mapOf(composer to setOf(
            Relationship(with = composee),
        ))
        every { mockNops.getResults() } returns mapOf(
            composer to Count(value = ApiComposition.MAX_COHESIVE_THRESHOLD - 1, type = "Int", unit = "operations"),
            composee to Count(value = 1, type = "Int", unit = "operations"),
        )
        every { mockNReadingOps.getResults() } returns mapOf(
            composer to Count(value = ApiComposition.MIN_READING_OPS_THRESHOLD - 1, type = "Int", unit = "operations")
        )

        // when
        underTest.run()
        val instances = underTest.getInstances()

        // then
        assertEquals(0, instances.size)
    }

    @Test
    fun `accountReadingOps ignores non-Microservice objects`() {
        // given
        val nonMicroserviceObject = mockk<Visitable>()
        every { mockNReadingOps.getResults() } returns mapOf(nonMicroserviceObject to Count(value = 1, type = "Int", unit = "operations"))
        every { mockNops.getResults() } returns emptyMap()
        every { mockSyncDeps.getResults() } returns emptyMap()

        // when
        underTest.run()
        val instances = underTest.getInstances()

        // then
        assertEquals(0, instances.size)
    }

}