package com.usvision.reports

import com.usvision.analyses.ArchitectureInsight
import com.usvision.analyses.Detector
import com.usvision.analyses.Measure
import com.usvision.analyses.Measurer
import com.usvision.model.CompanySystem
import com.usvision.model.Database
import com.usvision.model.Microservice
import com.usvision.model.Visitable

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