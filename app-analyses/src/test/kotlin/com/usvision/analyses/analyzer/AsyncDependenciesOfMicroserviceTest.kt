package com.usvision.analyses.analyzer

import com.usvision.model.domain.CompanySystem
import com.usvision.model.domain.MessageChannel
import kotlin.test.*
import com.usvision.model.domain.Microservice as Microservice

class AsyncDependenciesOfMicroserviceTest {
    private lateinit var underTest: AsyncDependenciesOfMicroservice

    @BeforeTest
    fun `create clean, new instance of AsyncConnectedOfMicroservice`() {
        underTest = AsyncDependenciesOfMicroservice()
    }

    @Test
    fun `it connects a single producer to a single consumer`() {
        // given
        val sys = CompanySystem(name = "test")
        val prod = Microservice(name = "producer")
        val cons = Microservice(name = "consumer")
        val channel = MessageChannel(name = "topic", id = "1234-abcd")
        sys.addSubsystem(prod)
        sys.addSubsystem(cons)
        prod.addPublishChannel(channel)
        cons.addSubscribedChannel(channel)

        // when
        sys.accept(underTest)
        val results = underTest.getResults()

        // then
        assertFalse { prod in results }
        assertTrue { cons in results }
        assertEquals(1, results[cons]!!.size)
        assertTrue { Relationship(with = prod) in results[cons]!! }
    }

    @Test
    fun `it connects two producers to a single consumer`() {
        // given
        val sys = CompanySystem(name = "test")
        val prod1 = Microservice(name = "producer1")
        val prod2 = Microservice(name = "producer2")
        val cons = Microservice(name = "consumer")
        val channel = MessageChannel(name = "topic", id = "1234-abcd")
        sys.addSubsystem(prod1)
        sys.addSubsystem(prod2)
        sys.addSubsystem(cons)
        prod1.addPublishChannel(channel)
        prod2.addPublishChannel(channel)
        cons.addSubscribedChannel(channel)

        // when
        sys.accept(underTest)
        val results = underTest.getResults()

        // then
        assertFalse { prod1 in results }
        assertFalse { prod2 in results }
        assertTrue { cons in results }
        assertEquals(2, results[cons]!!.size)
        assertTrue { Relationship(with = prod1) in results[cons]!! }
        assertTrue { Relationship(with = prod2) in results[cons]!! }
    }

    @Test
    fun `an isolated consumer doesnt get computed`() {
        // given
        val cons = Microservice(name = "consumer").also { it.run {
            addSubscribedChannel(MessageChannel(
                name = "topic", id = "1234-abcd"
            ))
            accept(underTest)
        } }

        // when
        val results = underTest.getResults()

        // then
        assertFalse { cons in results }
    }
}