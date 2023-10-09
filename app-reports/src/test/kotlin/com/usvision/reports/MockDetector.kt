package com.usvision.reports

import com.usvision.analyses.detector.ArchitectureInsight
import com.usvision.analyses.detector.Detector
import com.usvision.analyses.analyzer.Measure
import com.usvision.analyses.analyzer.Measurer
import com.usvision.model.domain.CompanySystem
import com.usvision.model.domain.databases.Database
import com.usvision.model.domain.Microservice
import com.usvision.model.visitor.Visitable

class MockAnalyzer : Measurer {
    override fun getResults(): Map<Visitable, Measure> {
        return emptyMap()
    }

    override fun visit(companySystem: CompanySystem) {}

    override fun visit(microservice: Microservice) {}

    override fun visit(database: Database) {}
}

class MockDetector(private val mockAnalyzer: MockAnalyzer) : Detector() {
    override fun collectMetrics() {}

    override fun combineMetric() {}

    override fun getInstances(): Set<ArchitectureInsight> {
        return emptySet()
    }
}

class AnotherMockDetector(private val mockAnalyzer: MockAnalyzer): Detector() {
    override fun collectMetrics() {}

    override fun combineMetric() {}

    override fun getInstances(): Set<ArchitectureInsight> {
        return emptySet()
    }

}