package com.sortinghat.pattern_detector.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object ServiceDependencies : IntIdTable() {
    val service = reference("service_id", Services)
    val serviceDepId = reference("service_dep_id", Services)

}