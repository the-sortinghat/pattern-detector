package com.usvision.analyses.analyzer

import com.usvision.model.domain.CompanySystem
import com.usvision.model.domain.Microservice
import com.usvision.model.domain.MessageChannel
import com.usvision.model.domain.databases.PostgreSQL
import com.usvision.model.domain.operations.RestEndpoint
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class NumberOfDependenciesTest {
    private lateinit var underTest: NumberOfDependencies

    @BeforeTest
    fun `create clean, new instance of NumberOfDependencies`() {
        underTest = NumberOfDependencies()
    }

    @Test
    fun `it gets 0 from a microservice with no dependencies`() {
        val sys = CompanySystem(name = "test")
        val ms = Microservice(name = "microservice")
        sys.addSubsystem(ms)

        sys.accept(underTest)
        val results = underTest.getResults()

        assertEquals(0, results[ms]?.value)
    }

    @Test
    fun `it gets 1 from a microservice with a single operation dependency`() {
        val sys = CompanySystem(name = "test")
        val ms = Microservice(name = "microservice")
        val op = RestEndpoint(httpVerb = "GET", path = "/data", "test")
        ms.consumeOperation(op)
        sys.addSubsystem(ms)

        sys.accept(underTest)
        val results = underTest.getResults()

        assertEquals(1, results[ms]?.value)
    }

    @Test
    fun `it gets 1 from a microservice with a single channel dependency and this channel with only one publisher`() {
        val sys = CompanySystem(name = "test")
        val ms = Microservice(name = "microservice")
        val pub1 = Microservice(name = "publisher1")
        val channel = MessageChannel(name = "channel", id = "1234-abcd")
        ms.addSubscribedChannel(channel)
        pub1.addPublishChannel(channel)
        sys.addSubsystem(ms)
        sys.addSubsystem(pub1)

        sys.accept(underTest)
        val results = underTest.getResults()

        assertEquals(1, results[ms]?.value)
    }

    @Test
    fun `it gets 2 from a microservice with a single channel dependency and this channel with two publishers`() {
        val sys = CompanySystem(name = "test")
        val ms = Microservice(name = "microservice")
        val pub1 = Microservice(name = "publisher1")
        val pub2 = Microservice(name = "publisher2")
        val channel = MessageChannel(name = "channel", id = "1234-abcd")
        ms.addSubscribedChannel(channel)
        pub1.addPublishChannel(channel)
        pub2.addPublishChannel(channel)
        sys.addSubsystem(ms)
        sys.addSubsystem(pub1)
        sys.addSubsystem(pub2)

        sys.accept(underTest)
        val results = underTest.getResults()

        assertEquals(2, results[ms]?.value)
    }

    @Test
    fun `it gets 1 from a microservice with a single database dependency`() {
        val sys = CompanySystem(name = "test")
        val ms = Microservice(name = "microservice")
        val db = PostgreSQL(id = "unique")
        ms.addDatabaseConnection(db)
        sys.addSubsystem(ms)

        sys.accept(underTest)
        val results = underTest.getResults()

        assertEquals(1, results[ms]?.value)
    }

    @Test
    fun `it gets 3 from a microservice with one of each type of dependency`() {
        val sys = CompanySystem(name = "test")
        val ms = Microservice(name = "microservice")
        val op = RestEndpoint(httpVerb = "GET", path = "/data", "test")
        val pub = Microservice(name = "publisher")
        val channel = MessageChannel(name = "channel", id = "1234-abcd")
        val db = PostgreSQL(id = "unique")
        ms.consumeOperation(op)
        ms.addSubscribedChannel(channel)
        pub.addPublishChannel(channel)
        ms.addDatabaseConnection(db)

        sys.addSubsystem(ms)
        sys.addSubsystem(pub)

        sys.accept(underTest)
        val results = underTest.getResults()

        assertEquals(3, results[ms]?.value)
    }

    @Test
    fun `it gets 4 from a microservice with one of each type of dependency, and one channel with two publishers`() {
        val sys = CompanySystem(name = "test")
        val ms = Microservice(name = "microservice")
        val op = RestEndpoint(httpVerb = "GET", path = "/data", "test")
        val pub1 = Microservice(name = "publisher1")
        val pub2 = Microservice(name = "publisher2")
        val channel = MessageChannel(name = "channel", id = "1234-abcd")
        val db = PostgreSQL(id = "unique")
        ms.consumeOperation(op)
        ms.addSubscribedChannel(channel)
        pub1.addPublishChannel(channel)
        pub2.addPublishChannel(channel)
        ms.addDatabaseConnection(db)

        sys.addSubsystem(ms)
        sys.addSubsystem(pub1)
        sys.addSubsystem(pub2)

        sys.accept(underTest)
        val results = underTest.getResults()

        assertEquals(4, results[ms]?.value)
    }
}