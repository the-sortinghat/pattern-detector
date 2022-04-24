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

        fun oneServiceWithTwoDatabases(): List<Visitable> {
            val service = Service(name = "foo", systemName = Slug.from("foo system"))
            val db1 = Database(name = "db 1", DataSource.MongoDb)
            val db2 = Database(name = "db 2", DataSource.PostgreSql)

            DatabaseUsage(service, database = db1, accessMode = DatabaseAccessMode.ReadWrite)
            DatabaseUsage(service, database = db2, accessMode = DatabaseAccessMode.ReadWrite)

            return listOf(service)
        }
    }
}