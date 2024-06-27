package com.usvision.analyses.analyzer

import com.usvision.model.domain.CompanySystem
import com.usvision.model.domain.MessageChannel
import com.usvision.model.domain.Microservice
import com.usvision.model.domain.operations.RestEndpoint
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class NumberOfDependentsTest {
    private lateinit var underTest: NumberOfDependents
    private lateinit var mockSync: SyncDependenciesOfMicroservice
    private lateinit var mockAsync: AsyncDependenciesOfMicroservice

    @BeforeTest
    fun `create clean, new instance of NumberOfDependents`() {
        MockKAnnotations.init(this)
        mockSync = mockk(relaxed = true)
        mockAsync = mockk(relaxed = true)
        underTest = NumberOfDependents(
            syncDependenciesOfMicroservice = mockSync,
            asyncDependenciesOfMicroservice = mockAsync
        )
    }

    @Test
    fun `it gets 0 from a microservice with no dependents`() {
        // given
        val sys = CompanySystem(name = "test")
        val ms = Microservice(name = "microservice")
        sys.addSubsystem(ms)

        every { mockSync.getResults() } returns emptyMap()
        every { mockAsync.getResults() } returns emptyMap()

        //when
        sys.accept(underTest)
        val results = underTest.getResults()

        //then
        assertEquals(0, results[ms]?.value)
    }

    @Test
    fun `it gets 1 from a microservice with a single sync dependent`() {
        //given
        val sys = CompanySystem(name = "test")
        val prod = Microservice(name = "producer")
        val cons = Microservice(name = "consumer")
        val operation = RestEndpoint(httpVerb = "GET", path = "/data", "test")
        sys.addSubsystem(prod)
        sys.addSubsystem(cons)
        prod.exposeOperation(operation)
        cons.consumeOperation(operation)

        every { mockSync.getResults() } returns mapOf(
            cons to setOf(Relationship(with = prod))
        )
        every {mockAsync.getResults() } returns emptyMap()

        //when
        sys.accept(underTest)
        val results = underTest.getResults()

        //then
        assertEquals(1, results[prod]?.value)
    }

    @Test
    fun `it gets 1 from a microservice with a single sync dependent consuming two operations`() {
        //given
        val sys = CompanySystem(name = "test")
        val prod = Microservice(name = "producer")
        val cons = Microservice(name = "consumer")
        val operation1 = RestEndpoint(httpVerb = "GET", path = "/data", "test")
        val operation2 = RestEndpoint(httpVerb = "POST", path = "/data", "test")
        sys.addSubsystem(prod)
        sys.addSubsystem(cons)
        prod.exposeOperation(operation1)
        prod.exposeOperation(operation2)
        cons.consumeOperation(operation1)
        cons.consumeOperation(operation2)

        every { mockSync.getResults() } returns mapOf(
            cons to setOf(Relationship(with = prod))
        )
        every {mockAsync.getResults() } returns emptyMap()

        //when
        sys.accept(underTest)
        val results = underTest.getResults()

        //then
        assertEquals(1, results[prod]?.value)
    }

    @Test
    fun `it gets 2 from a microservice with two sync dependent consuming the same operation`() {
        //given
        val sys = CompanySystem(name = "test")
        val prod = Microservice(name = "producer")
        val cons1 = Microservice(name = "consumer1")
        val cons2 = Microservice(name = "consumer2")
        val operation1 = RestEndpoint(httpVerb = "GET", path = "/data", "test")
        sys.addSubsystem(prod)
        sys.addSubsystem(cons1)
        sys.addSubsystem(cons2)
        prod.exposeOperation(operation1)
        cons1.consumeOperation(operation1)
        cons2.consumeOperation(operation1)

        every { mockSync.getResults() } returns mapOf(
            cons1 to setOf(Relationship(with = prod)),
            cons2 to setOf(Relationship(with = prod))
        )
        every {mockAsync.getResults() } returns emptyMap()

        //when
        sys.accept(underTest)
        val results = underTest.getResults()

        //then
        assertEquals(2, results[prod]?.value)
    }

    @Test
    fun `it gets 2 from a microservice with a two sync dependent consuming the two operations`() {
        //given
        val sys = CompanySystem(name = "test")
        val prod = Microservice(name = "producer")
        val cons1 = Microservice(name = "consumer1")
        val cons2 = Microservice(name = "consumer2")
        val operation1 = RestEndpoint(httpVerb = "GET", path = "/data", "test")
        val operation2 = RestEndpoint(httpVerb = "POST", path = "/data", "test")
        sys.addSubsystem(prod)
        sys.addSubsystem(cons1)
        sys.addSubsystem(cons2)
        prod.exposeOperation(operation1)
        prod.exposeOperation(operation2)
        cons1.consumeOperation(operation1)
        cons2.consumeOperation(operation2)

        every { mockSync.getResults() } returns mapOf(
            cons1 to setOf(Relationship(with = prod)),
            cons2 to setOf(Relationship(with = prod))
        )
        every {mockAsync.getResults() } returns emptyMap()

        //when
        sys.accept(underTest)
        val results = underTest.getResults()

        //then
        assertEquals(2, results[prod]?.value)
    }

    @Test
    fun `it gets 1 from a microservice with a single async dependent`() {
        //given
        val sys = CompanySystem(name = "test")
        val prod = Microservice(name = "producer")
        val cons = Microservice(name = "consumer")
        val channel = MessageChannel(name = "topic", id = "1234-abcd")
        sys.addSubsystem(prod)
        sys.addSubsystem(cons)
        prod.addPublishChannel(channel)
        cons.addSubscribedChannel(channel)

        every {mockSync.getResults() } returns emptyMap()

        every {mockAsync.getResults() } returns mapOf(
            cons to setOf(Relationship(with = prod))
        )

        //when
        sys.accept(underTest)
        val results = underTest.getResults()

        //then
        assertEquals(1, results[prod]?.value)
    }

    @Test
    fun `it gets 1 from a microservice with a single async dependent, subscribing two channels`() {
        //given
        val sys = CompanySystem(name = "test")
        val prod = Microservice(name = "producer")
        val cons = Microservice(name = "consumer")
        val channel1 = MessageChannel(name = "topic", id = "1234-abcd")
        val channel2 = MessageChannel(name = "topic", id = "5678-efgh")
        sys.addSubsystem(prod)
        sys.addSubsystem(cons)
        prod.addPublishChannel(channel1)
        prod.addPublishChannel(channel2)
        cons.addSubscribedChannel(channel1)
        cons.addSubscribedChannel(channel2)

        every {mockSync.getResults() } returns emptyMap()

        every {mockAsync.getResults() } returns mapOf(
            cons to setOf(Relationship(with = prod))
        )

        //when
        sys.accept(underTest)
        val results = underTest.getResults()

        //then
        assertEquals(1, results[prod]?.value)
    }

    @Test
    fun `it gets 2 from a microservice with two async dependent, subscribing the same channel`() {
        //given
        val sys = CompanySystem(name = "test")
        val prod = Microservice(name = "producer")
        val cons1 = Microservice(name = "consumer1")
        val cons2 = Microservice(name = "consumer2")
        val channel1 = MessageChannel(name = "topic", id = "1234-abcd")
        sys.addSubsystem(prod)
        sys.addSubsystem(cons1)
        sys.addSubsystem(cons2)
        prod.addPublishChannel(channel1)
        cons1.addSubscribedChannel(channel1)
        cons2.addSubscribedChannel(channel1)

        every {mockSync.getResults() } returns emptyMap()

        every {mockAsync.getResults() } returns mapOf(
            cons1 to setOf(Relationship(with = prod)),
            cons2 to setOf(Relationship(with = prod))
        )

        //when
        sys.accept(underTest)
        val results = underTest.getResults()

        //then
        assertEquals(2, results[prod]?.value)
    }

    @Test
    fun `it gets 2 from a microservice with two async dependent, subscribing two channels`() {
        //given
        val sys = CompanySystem(name = "test")
        val prod = Microservice(name = "producer")
        val cons1 = Microservice(name = "consumer1")
        val cons2 = Microservice(name = "consumer2")
        val channel1 = MessageChannel(name = "topic", id = "1234-abcd")
        val channel2 = MessageChannel(name = "topic", id = "5678-efgh")
        sys.addSubsystem(prod)
        sys.addSubsystem(cons1)
        sys.addSubsystem(cons2)
        prod.addPublishChannel(channel1)
        prod.addPublishChannel(channel2)
        cons1.addSubscribedChannel(channel1)
        cons2.addSubscribedChannel(channel2)

        every {mockSync.getResults() } returns emptyMap()

        every {mockAsync.getResults() } returns mapOf(
            cons1 to setOf(Relationship(with = prod)),
            cons2 to setOf(Relationship(with = prod))
        )

        //when
        sys.accept(underTest)
        val results = underTest.getResults()

        //then
        assertEquals(2, results[prod]?.value)
    }

}