package com.usvision.analyses.detector

import com.usvision.analyses.analyzer.*
import com.usvision.model.domain.Microservice
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class DependentHubTest {

    private lateinit var underTest: DependentHub

    @BeforeTest
    fun `create clean, new instance of DependentHub`() {
        MockKAnnotations.init(this)
        underTest = DependentHub(
            numberOfDependencies = mockk()
        )
    }

    @Test
    fun `it detects 1 when there is a dependent hub`() {
        // given
        val ms1 = Microservice(name = "mock1")
        val ms2 = Microservice(name = "mock2")
        val ms3 = Microservice(name = "mock3")

        val mockNumberOfDependencies: NumberOfDependencies = mockk()
        every { mockNumberOfDependencies.getResults() } returns mapOf(
            ms1 to Count(value = DependentHub.DEPENDENCY_THRESHOLD + 1, type = "Int", unit = "dependencies"),
            ms2 to Count(value = 2, type = "Int", unit = "dependencies"),
            ms3 to Count(value = 1, type = "Int", unit = "dependencies")
        )

        underTest = DependentHub(numberOfDependencies = mockNumberOfDependencies)

        // when
        underTest.run()
        val instances = underTest.getInstances()

        // then
        assertEquals(1, instances.size)
    }

}