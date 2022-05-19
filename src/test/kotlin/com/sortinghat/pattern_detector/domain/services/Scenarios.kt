package com.sortinghat.pattern_detector.domain.services

import com.sortinghat.pattern_detector.domain.behaviors.Visitable
import com.sortinghat.pattern_detector.domain.model.*
import com.sortinghat.pattern_detector.domain.model.patterns.AsyncMessage

class Scenarios {
    companion object {
        fun oneServiceOneOperation(): List<Visitable> {
            val service = Service(name = "foo", systemName =  Slug.from("foo service"))
            val operation = Operation(verb = HttpVerb.GET, uri = "/foo")

            service.expose(operation)

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

        fun oneModuleWithOneService(): List<Visitable> {
            val explicitModule = Module()

            return listOf(Service(
                name = "foo",
                systemName = Slug.from("foo system"),
                module = explicitModule
            ))
        }

        fun oneModuleWithVeryLargeService(): List<Visitable> {
            val explicitModule = Module()

            val svc = Service(
                name = "foo",
                systemName = Slug.from("foo system"),
                module = explicitModule
            )

            for (i in 1..20)
                svc.expose(Operation(HttpVerb.GET, "/oper_$i"))

            return listOf(svc)
        }

        fun oneQueryServiceWithTwoSyncDependencies(): List<Visitable> {
            val systemName = Slug.from("foo system")
            val querySvc = Service(name = "query", systemName)
            val dep1 = Service(name = "dep 1", systemName)
            val dep2 = Service(name = "dep 2", systemName)

            val queryOp = Operation(HttpVerb.GET, "/foo")
            val partialQuery1 = Operation(HttpVerb.GET, "/bar")
            val partialQuery2 = Operation(HttpVerb.GET, "/baz")

            querySvc.expose(queryOp)
            dep1.expose(partialQuery1)
            dep2.expose(partialQuery2)

            listOf(partialQuery1, partialQuery2).forEach { partial ->
                querySvc.consume(partial)
            }

            return listOf(querySvc, dep1, dep2)
        }

        fun onePublisherOneSubscriber(): List<Visitable> {
            val systemName = Slug.from("foo system")
            val publisher = Service(name = "pub", systemName)
            val subscriber = Service(name = "sub", systemName)

            val channel = MessageChannel("topic")

            publisher.publishTo(channel)
            subscriber.listenTo(channel)

            return listOf(publisher, subscriber)
        }

        fun onePublisherTwoSubscribers(): List<Visitable> {
            val systemName = Slug.from("foo system")
            val publisher = Service(name = "pub", systemName)
            val subscriber1 = Service(name = "sub1", systemName)
            val subscriber2 = Service(name = "sub2", systemName)

            val channel = MessageChannel("topic")

            publisher.publishTo(channel)
            subscriber1.listenTo(channel)
            subscriber2.listenTo(channel)

            return listOf(publisher, subscriber1, subscriber2)
        }

        fun cqrsBetweenTwoServices(): CQRSScenario {
            val slug = Slug.from("foo system")
            val command = Service(name = "command", slug)
            val query = Service(name = "query", slug)

            command.expose(Operation(HttpVerb.POST, "/foos"))
            query.expose(Operation(HttpVerb.GET, "/foos"))

            val channel = MessageChannel(name = "topic")

            command.publishTo(channel)
            query.listenTo(channel)

            return CQRSScenario(
                listOf(command, query),
                setOf(AsyncMessage.from(command, query))
            )
        }

        fun twoCqrsEachNonTrivial(): CQRSScenario {
            val slug = Slug.from("foo system")

            val commandA1 = Service(name = "commandA1", slug)
            val commandA2 = Service(name = "commandA2", slug)
            val queryA = Service(name = "queryA", slug)

            commandA1.expose(Operation(HttpVerb.POST, "/foos"))
            commandA2.expose(Operation(HttpVerb.POST, "/bazes"))
            queryA.expose(Operation(HttpVerb.GET, "/foos/:id/bazes"))

            val channelA1 = MessageChannel(name = "topicA1")
            val channelA2 = MessageChannel(name = "topicA2")

            commandA1.publishTo(channelA1)
            commandA2.publishTo(channelA2)
            queryA.listenTo(channelA1)
            queryA.listenTo(channelA2)

            val commandB1 = Service(name = "commandB1", slug)
            val commandB2 = Service(name = "commandB2", slug)
            val queryB = Service(name = "queryB", slug)

            commandB1.expose(Operation(HttpVerb.POST, "/foo"))
            commandB2.expose(Operation(HttpVerb.POST, "/baz"))
            queryB.expose(Operation(HttpVerb.GET, "/foo/:id/baz"))

            val channelB1 = MessageChannel(name = "topicB1")
            val channelB2 = MessageChannel(name = "topicB2")

            commandA1.publishTo(channelB1)
            commandA2.publishTo(channelB2)
            queryA.listenTo(channelB1)
            queryA.listenTo(channelB2)

            return CQRSScenario(
                listOf(commandA1, commandA2, queryA, commandB1, commandB2, queryB),
                setOf(
                    AsyncMessage.from(commandA1, queryA),
                    AsyncMessage.from(commandA2, queryA),
                    AsyncMessage.from(commandB1, queryB),
                    AsyncMessage.from(commandB2, queryB),
                )
            )
        }
    }
}