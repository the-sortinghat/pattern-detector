package com.usvision.reports

import com.usvision.analyses.detector.Detector
import com.usvision.reports.exceptions.ClassIsNotDetectorException
import com.usvision.reports.exceptions.DetectorNotFoundException
import com.usvision.reports.exceptions.UnknownPresetException
import com.usvision.reports.executioner.PlanExecutioner
import com.usvision.reports.executioner.SequentialPlanExecutioner
import com.usvision.reports.planner.AnalyzerReusePlanner
import com.usvision.reports.planner.Planner
import com.usvision.reports.utils.Report
import com.usvision.reports.utils.ReportRequest
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType

class ReportSupervisor(
    private val systemRepository: SystemRepository,
    private val planner: Planner = AnalyzerReusePlanner(),
    private val planExecutioner: PlanExecutioner = SequentialPlanExecutioner()
) {

    class ReportRequestGenerator(private val packageName: String = "com.usvision.analyses.detector") {
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

    // TODO: consider "" as a preset for all detectors
    private val presets: Map<String,Set<String>> = mapOf(
        "" to setOf("DatabasePerService")
    )

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
