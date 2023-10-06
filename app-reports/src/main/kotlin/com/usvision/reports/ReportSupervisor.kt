package com.usvision.reports

import com.usvision.analyses.Detector
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType

interface Report

data class ReportRequest(
    val detectors: Set<KClass<Detector>>
)

class ReportSupervisor(
    private val systemRepository: SystemRepository
) {

    class ReportRequestGenerator(private val packageName: String = "com.usvision.analyses") {
        fun generate(detectorsNames: Set<String>) = detectorsNames
            .map(this::parse)
            .let { ReportRequest(detectors = it.toSet()) }

        fun parse(detectorName: String): KClass<Detector> {
            val qualifiedName = "$packageName.$detectorName"

            return getKClass(qualifiedName).also {
                ensureIsDetector(it)
            } as KClass<Detector>
        }

        private fun ensureIsDetector(detectorKClass: KClass<out Any>) {
            val givenStarProjectedType: KType = detectorKClass.starProjectedType
            val expectedStarProjectedType: KType = Detector::class.starProjectedType

            val givenIsDetector = givenStarProjectedType.isSubtypeOf(expectedStarProjectedType)

            if (!givenIsDetector)
                throw ClassIsNotDetectorException(detectorKClass.qualifiedName.toString())
        }

        private fun getKClass(qualifiedName: String): KClass<out Any> = try {
            Class.forName(qualifiedName).kotlin
        } catch (cnf: ClassNotFoundException) {
            throw DetectorNotFoundException(qualifiedName)
        }
    }

    fun generateReport(detectorsNames: Set<String>): Report? {
        val reportRequest = ReportRequestGenerator().generate(detectorsNames)
        return null
    }

    fun generateReport(presetName: String): Report? {
        return generateReport(detectorsNames = resolvePreset(presetName))
    }

    private fun resolvePreset(presetName: String): Set<String> {
        TODO("Not yet implemented")
    }
}
