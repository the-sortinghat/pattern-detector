package com.sortinghat.pattern_detector.domain.model

class DatabasePerService(
    service: Service,
    database: Database
) {
    val service: String
    val database: String

    init {
        this.service = service.name
        this.database = database.name
    }
}
