package com.usvision.reports

import com.usvision.analyses.detector.Detector
import com.usvision.reports.exceptions.ClassIsNotDetectorException
import com.usvision.reports.exceptions.DetectorNotFoundException
import com.usvision.reports.exceptions.UnknownPresetException
import com.usvision.reports.executioner.PlanExecutioner
import com.usvision.reports.executioner.SequentialPlanExecutioner
import com.usvision.reports.planner.AnalyzerReusePlanner
import com.usvision.reports.planner.Planner
import com.usvision.reports.utils.DetectorsLocator
import com.usvision.reports.utils.Report
import com.usvision.reports.utils.ReportRequest
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

class ReportSupervisor(
    private val systemRepository: SystemRepository,
    private val presets: Map<String,Set<String>>,
    private val planner: Planner = AnalyzerReusePlanner(),
    private val planExecutioner: PlanExecutioner = SequentialPlanExecutioner(),
    private val detectorsLocator: DetectorsLocator = DetectorsLocator()
) {

    class ReportRequestGenerator(private val packageName: String = "com.usvision.analyses.detector") {
        fun generate(detectorsNames: Set<String>) = detectorsNames
            .map(this::parse)
            .let { ReportRequest(detectors = it.toSet()) }

        fun parse(qualifiedName: String): KClass<Detector> {
            val isRightPackage = qualifiedName.contains(this.packageName)

            if (!isRightPackage)
                throw ClassIsNotDetectorException(qualifiedName)

            return getKClass(qualifiedName).also {
                ensureIsDetector(it)
            } as KClass<Detector>
        }

        private fun ensureIsDetector(detectorKClass: KClass<out Any>) {
            val givenIsDetector = Detector::class.isSuperclassOf(detectorKClass)

            if (!givenIsDetector)
                throw ClassIsNotDetectorException(detectorKClass.qualifiedName.toString())
        }

        private fun getKClass(qualifiedName: String): KClass<out Any> = try {
            Class.forName(qualifiedName).kotlin
        } catch (cnf: ClassNotFoundException) {
            throw DetectorNotFoundException(qualifiedName)
        }
    }

    fun generateReport(detectorsNames: Set<String>, systemName: String): Report {
        val detectorsQualifiedNames = parseAllToQualifiedName(detectorsNames)
        val reportRequest = ReportRequestGenerator().generate(detectorsQualifiedNames)
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

    fun getDetectors(): Set<String> = detectorsLocator.getAllSimpleName()

    private fun parseAllToQualifiedName(simpleNames: Set<String>): Set<String> {
        return simpleNames
            .map { this.detectorsLocator.parseToQualifiedName(it)!! }
            .toSet()
    }

    private fun resolvePreset(presetName: String): Set<String> {
        return presets[presetName]
            ?: throw UnknownPresetException(presetName)
    }
}
