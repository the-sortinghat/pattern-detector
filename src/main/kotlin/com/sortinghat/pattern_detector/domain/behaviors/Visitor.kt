package com.sortinghat.pattern_detector.domain.behaviors

import com.sortinghat.pattern_detector.domain.model.Database
import com.sortinghat.pattern_detector.domain.model.DatabaseUsage
import com.sortinghat.pattern_detector.domain.model.Operation
import com.sortinghat.pattern_detector.domain.model.Service

interface Visitor {
    fun visit(service: Service)

    fun visit(operation: Operation)

    fun visit(database: Database)

    fun visit(usage: DatabaseUsage)
}