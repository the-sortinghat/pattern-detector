package com.sortinghat.pattern_detector.domain.services

import com.sortinghat.pattern_detector.domain.behaviors.Visitable
import com.sortinghat.pattern_detector.domain.model.*

class Scenarios {
    companion object {
        fun oneServiceOneOperation(): List<Visitable> {
            val service = Service(name = "foo", systemName =  Slug.from("foo service"))
            val operation = Operation(verb = HttpVerb.GET, uri = "/foo")

            service.addOperation(operation)

            return listOf(service)
        }

        fun oneServiceOneDatabase(): List<Visitable> {
            val service = Service(name = "foo", systemName =  Slug.from("foo service"))
            val database = Database(name = "foo db", DataSource.MongoDb)

            DatabaseUsage(service, database, DatabaseAccessMode.ReadWrite)

            return listOf(service)
        }
    }
}