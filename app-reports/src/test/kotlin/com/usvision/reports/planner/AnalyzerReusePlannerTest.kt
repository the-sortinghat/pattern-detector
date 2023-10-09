package com.usvision.reports.planner

import com.usvision.reports.*
import com.usvision.reports.utils.Plan
import com.usvision.reports.utils.ReportRequest
import kotlin.test.*

class AnalyzerReusePlannerTest {
    private lateinit var underTest: AnalyzerReusePlanner

    @BeforeTest
    fun `create clean, new instance of AnalyzerReusePlanner`() {
        underTest = AnalyzerReusePlanner()
    }

    @Test
    fun `it identifies MockAnalyzer as dependency of MockDetector`() {
        // given
        val req = ReportRequest(setOf(MockDetector::class))

        // when
        val result = underTest.plan(req)

        // then
        assertIs<Plan>(result)
        assertEquals(result.analyzers.size, 1)
        assertIs<MockAnalyzer>(result.analyzers.first())
    }

    @Test
    fun `it identifies MockAnalyzer the single analyzer reused by two detectors`() {
        // given
        val req = ReportRequest(setOf(
            MockDetector::class,
            AnotherMockDetector::class
        ))

        // when
        val result = underTest.plan(req)

        // then
        assertIs<Plan>(result)
        assertEquals(result.analyzers.size, 1)
        assertIs<MockAnalyzer>(result.analyzers.first())
    }
}