package com.usvision.reports

import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.BeforeTest
import kotlin.test.Test

class MockClassNotDetector {}

internal class ReportRequestGeneratorTest {
    private lateinit var underTest: ReportSupervisor.ReportRequestGenerator

    @BeforeTest
    fun `create clean, new instance of ReportRequestGenerator`() {
        underTest = ReportSupervisor.ReportRequestGenerator(packageName = "com.usvision.reports")
    }

    @Test
    fun `it throws DetectorNotFound when class not found`() {
        // given
        val nonExistingClassName = "FooClassNonExisting"

        // when ... then
        assertThrows<DetectorNotFoundException> {
            underTest.parse(nonExistingClassName)
        }
    }

    @Test
    fun `it throws ClassIsNotDetector when class is not Detector`() {
        // given
        val existingButNotDetector =
            MockClassNotDetector::class.simpleName.toString()

        // when ... then
        assertThrows<ClassIsNotDetectorException> {
            underTest.parse(existingButNotDetector)
        }
    }

    @Test
    fun `it does not throw when class is Detector`() {
        // given
        val existingDetector =
            MockDetector::class.simpleName.toString()
        println(existingDetector)

        // when ... then
        assertDoesNotThrow { 
            underTest.parse(existingDetector)
        }
    }
}