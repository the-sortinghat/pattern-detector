package com.usvision.reports.executioner

import com.usvision.analyses.detector.ArchitectureInsight
import com.usvision.model.domain.CompanySystem
import com.usvision.model.domain.databases.Database
import com.usvision.model.domain.Microservice
import com.usvision.model.systemcomposite.System
import com.usvision.reports.MockAnalyzer
import com.usvision.reports.MockDetector
import com.usvision.reports.utils.Plan
import com.usvision.reports.utils.Report
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlin.test.*

@Suppress("unused")
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