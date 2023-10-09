package com.usvision.analyses.analyzer

import com.usvision.model.domain.CompanySystem
import com.usvision.model.domain.Microservice
import kotlin.test.*

internal class MicroserviceBelongingAnalyzerTest {

    private lateinit var underTest: MicroserviceBelongingAnalyzer

    @BeforeTest
    fun `create clean, new instance of MicroserviceBelongingAnalyzer`() {
        underTest = MicroserviceBelongingAnalyzer()
    }

    @Test
    fun `it indicates CompSys S as parent of MiSvc M when M is subsystem of S`() {
        // given
        val S = CompanySystem(name = "company")
        val M = Microservice(name = "microservice")
        S.addSubsystem(M)

        // when
        S.accept(underTest)
        val result = underTest.getResults()

        // then
        assertTrue { M in result }
        assertEquals(result[M]!!.with, S)
    }

    @Test
    fun `it does not indicate CompSys S as parent of MiSvc when M is sub-subsytem of S`() {
        // given
        val S = CompanySystem(name = "company")
        val subsys = CompanySystem(name = "subdomain")
        val M = Microservice(name = "microservice")
        S.addSubsystem(subsys)
        subsys.addSubsystem(M)

        // when
        S.accept(underTest)
        val result = underTest.getResults()

        // then
        assertTrue { M in result }
        assertNotEquals(result[M]!!.with, S)
        assertEquals(result[M]!!.with, subsys)
    }
}