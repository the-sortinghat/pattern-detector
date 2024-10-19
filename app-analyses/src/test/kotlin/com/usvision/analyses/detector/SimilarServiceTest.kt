package com.usvision.analyses.detector

import com.usvision.analyses.analyzer.*
import com.usvision.model.domain.Microservice
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class SimilarServiceTest {

    private lateinit var underTest: SimilarService

    @BeforeTest
    fun `create clean, new instance of SimilarService`() {
        MockKAnnotations.init(this)
        underTest = SimilarService(
            coincidenceAnalyser = mockk()
        )
    }

    @Test
    fun `it gets 0 when there is no similar service`() {
        // given
        val ms1 = Microservice(name = "mock1")
        val ms2 = Microservice(name = "mock2")
        val ms3 = Microservice(name = "mock3")

        val mockCoincidenceAnalyser: CoincidenceOfMicroservices = mockk()
        every { mockCoincidenceAnalyser.getDetailedResults() } returns mapOf(
            ms1 to listOf(Pair(Relationship(ms2), "sync")),
            ms2 to listOf(Pair(Relationship(ms1), "sync")),
            ms3 to listOf(Pair(Relationship(ms1), "sync"))
        )

        underTest = SimilarService(coincidenceAnalyser = mockCoincidenceAnalyser)

        // when
        underTest.run()
        val instances = underTest.getInstances()

        // then
        assertEquals(0, instances.size)
    }

    @Test
    fun `it detects 0 when there is two similarities sync and async`() {
        // given
        val ms1 = Microservice(name = "mock1")
        val ms2 = Microservice(name = "mock2")
        val ms3 = Microservice(name = "mock3")

        val mockCoincidenceAnalyser: CoincidenceOfMicroservices = mockk()
        every { mockCoincidenceAnalyser.getDetailedResults() } returns mapOf(
            ms1 to listOf(Pair(Relationship(ms2), "sync"), Pair(Relationship(ms2), "async")),
            ms2 to listOf(Pair(Relationship(ms1), "sync"), Pair(Relationship(ms1), "async")),
            ms3 to listOf(Pair(Relationship(ms1), "sync"))
        )

        underTest = SimilarService(coincidenceAnalyser = mockCoincidenceAnalyser)

        // when
        underTest.run()
        val instances = underTest.getInstances()

        // then
        assertEquals(0, instances.size)
    }

    @Test
    fun `it detects 1 when there is a similar service with one of each type of coincidence`() {
        // given
        val ms1 = Microservice(name = "mock1")
        val ms2 = Microservice(name = "mock2")
        val ms3 = Microservice(name = "mock3")

        val mockCoincidenceAnalyser: CoincidenceOfMicroservices = mockk()
        every { mockCoincidenceAnalyser.getDetailedResults() } returns mapOf(
            ms1 to listOf(Pair(Relationship(ms2), "sync"), Pair(Relationship(ms2), "async"), Pair(Relationship(ms2), "database")),
            ms2 to listOf(Pair(Relationship(ms1), "sync"), Pair(Relationship(ms1), "async"), Pair(Relationship(ms1), "database")),
            ms3 to listOf(Pair(Relationship(ms1), "sync"))
        )

        underTest = SimilarService(coincidenceAnalyser = mockCoincidenceAnalyser)

        // when
        underTest.run()
        val instances = underTest.getInstances()

        // then
        assertEquals(1, instances.size)
    }

}