package com.usvision.reports

import com.usvision.analyses.ArchitectureInsight
import com.usvision.analyses.Detector
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType

class Report(
    private val value: Map<KClass<out ArchitectureInsight>, Any>
) : Map<KClass<out ArchitectureInsight>, Any> by value

data class ReportRequest(
    val detectors: Set<KClass<out Detector>>
)

class ReportSupervisor(
    private val systemRepository: SystemRepository,
    private val planner: Planner = AnalyzerReusePlanner(),
    private val planExecutioner: PlanExecutioner = SequentialPlanExecutioner()
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

    private val presets: Map<String,Set<String>> = emptyMap()

    fun generateReport(detectorsNames: Set<String>, systemName: String): Report {
        val reportRequest = ReportRequestGenerator().generate(detectorsNames)
        val plan = planner.plan(reportRequest)
        val system = systemRepository.load(systemName)
        return planExecutioner.execute(plan, system)
    }

    fun generateReport(presetName: String, systemName: String): Report {
        return generateReport(
            detectorsNames = resolvePreset(presetName),
            systemName = systemName
        )
    }

    fun getPresets(): Set<String> = presets.keys.toSet()

    private fun resolvePreset(presetName: String): Set<String> = presets[presetName]
        ?: throw UnknownPresetException(presetName)
}
