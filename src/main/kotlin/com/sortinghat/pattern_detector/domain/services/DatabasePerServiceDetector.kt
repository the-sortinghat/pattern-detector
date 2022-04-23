package com.sortinghat.pattern_detector.domain.services

import com.sortinghat.pattern_detector.domain.behaviors.Visitor
import com.sortinghat.pattern_detector.domain.model.Database
import com.sortinghat.pattern_detector.domain.model.DatabaseUsage
import com.sortinghat.pattern_detector.domain.model.Operation
import com.sortinghat.pattern_detector.domain.model.Service

@Suppress("unused")
class DatabasePerServiceDetector : Visitor {
    override fun visit(service: Service) {
    }

    override fun visit(operation: Operation) {
    }

    override fun visit(database: Database) {
    }

    override fun visit(usage: DatabaseUsage) {
    }
}