package com.sortinghat.pattern_detector.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object ConsumedOperations : IntIdTable() {
    val operationId = reference("operation_id", Operations)
    val consumerId = reference("consumer_id", Services)

    init {
        uniqueIndex(operationId, consumerId)
    }
}