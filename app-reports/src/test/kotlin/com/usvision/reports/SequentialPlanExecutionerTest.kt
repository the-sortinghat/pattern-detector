package com.usvision.reports

import com.usvision.analyses.ArchitectureInsight
import com.usvision.model.CompanySystem
import com.usvision.model.Database
import com.usvision.model.Microservice
import com.usvision.model.System
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import kotlin.test.*

class MockInsight(val system: System) : ArchitectureInsight

internal class SequentialPlanExecutionerTest {
    private lateinit var underTest: SequentialPlanExecutioner

    @MockK
    private lateinit var mockAnalyzer: MockAnalyzer
    @MockK
    private lateinit var mockDetector: MockDetector

    @BeforeTest
    fun `create clean, new instance of SequentialPlanExecutioner`() {
        underTest = SequentialPlanExecutioner()
        mockAnalyzer = MockAnalyzer()
        mockDetector = MockDetector(mockAnalyzer)
        MockKAnnotations.init(this)
    }

    @Test
    fun `given a simple Plan, it gives the adequate report`() {
        // given
        val system = CompanySystem(name = "mock")
        val plan = Plan(
            analyzers = setOf(mockAnalyzer),
            detectors = setOf(mockDetector)
        )
        every { mockDetector.getInstances() } returns setOf(MockInsight(system))
        every { mockDetector.run() } returns Unit
        every { mockAnalyzer.visit(any() as CompanySystem) } returns Unit
        every { mockAnalyzer.visit(any() as Microservice) } returns Unit
        every { mockAnalyzer.visit(any() as Database) } returns Unit

        // when
        val result = underTest.execute(plan, system)

        // then
        assertIs<Report>(result)
        assertIs<List<ArchitectureInsight>>(result[MockInsight::class])
        assertEquals((result[MockInsight::class] as List<*>).size, 1)
    }
}