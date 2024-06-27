package com.usvision.analyses.detector

import com.usvision.analyses.analyzer.Count
import com.usvision.analyses.analyzer.NumberOfDependents
import com.usvision.model.domain.Microservice
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class CentralizedServiceTest {

    private lateinit var underTest: CentralizedService
    private lateinit var mockNumberOfDependents: NumberOfDependents

    @BeforeTest
    fun `create clean, new instance of CentralizedService`() {
        MockKAnnotations.init(this)
        mockNumberOfDependents = mockk()
        underTest = CentralizedService(
            numberOfDependents = mockNumberOfDependents
        )
    }

    @Test
    fun `it gets 0 when there is no centralized service instance`() {
        // given
        val ms1 = Microservice(name = "mock1")
        val ms2 = Microservice(name = "mock2")
        val ms3 = Microservice(name = "mock3")

        every { mockNumberOfDependents.getResults() } returns mapOf(
            ms1 to Count(value = 1, type = "Int", unit = "dependents"),
            ms2 to Count(value = 2, type = "Int", unit = "dependents"),
            ms3 to Count(value = 1, type = "Int", unit = "dependents")
        )

        // when
        underTest.run()
        val instances = underTest.getInstances()

        // then
        assertEquals(0, instances.size)
    }

    @Test
    fun `it detects 1 when there is a centralized service instance`() {
        // given
        val ms1 = Microservice(name = "mock1")
        val ms2 = Microservice(name = "mock2")
        val ms3 = Microservice(name = "mock3")

        every { mockNumberOfDependents.getResults() } returns mapOf(
            ms1 to Count(value = CentralizedService.DEPENDENT_THRESHOLD + 1, type = "Int", unit = "dependents"),
            ms2 to Count(value = 2, type = "Int", unit = "dependents"),
            ms3 to Count(value = 1, type = "Int", unit = "dependents")
        )

        // when
        underTest.run()
        val instances = underTest.getInstances()

        // then
        assertEquals(1, instances.size)
    }
}