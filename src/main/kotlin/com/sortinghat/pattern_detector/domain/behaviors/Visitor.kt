package com.sortinghat.pattern_detector.domain.behaviors

import com.sortinghat.pattern_detector.domain.model.*

interface Visitor {
    fun visit(service: Service)

    fun visit(operation: Operation)

    fun visit(database: Database)

    fun visit(usage: DatabaseUsage)

    fun visit(module: Module)

    fun visit(channel: MessageChannel)

    fun visit(dependencies: ServiceDependency)
}