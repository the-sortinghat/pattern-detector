package com.usvision.analyses.detector

import com.usvision.analyses.analyzer.Count
import com.usvision.analyses.analyzer.NumberOfExposedOperations
import com.usvision.analyses.analyzer.NumberOfReadingExposedOperations
import com.usvision.analyses.analyzer.NumberOfWritingExposedOperations
import com.usvision.model.domain.Microservice
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CqrsTest {
    private lateinit var underTest: Cqrs

    @MockK
    private lateinit var nops: NumberOfExposedOperations

    @MockK
    private lateinit var nReadOps: NumberOfReadingExposedOperations

    @MockK
    private lateinit var nWriteOps: NumberOfWritingExposedOperations

    @MockK
    private lateinit var asyncMessaging: AsyncMessaging

    @BeforeTest
    fun `create clean, new instance of Cqrs`() {
        MockKAnnotations.init(this)
        underTest = Cqrs(
            nops = this.nops,
            nReadOps = this.nReadOps,
            nWriteOps = this.nWriteOps,
            asyncMessaging = this.asyncMessaging
        )
    }

    @Test
    fun `a single microservice does not get detected`() {
        // given
        val microservice = Microservice(name = "test")
        every { nops.getResults() } returns mapOf(microservice to Count(
            value = 1, type = "Int", unit = "operations"
        ))
        every { nReadOps.getResults() } returns mapOf(microservice to Count(
            value = 1, type = "Int", unit = "operations"
        ))
        every { nWriteOps.getResults() } returns emptyMap()
        every { asyncMessaging.getInstances() } returns emptySet()

        // when
        underTest.run()
        val instances = underTest.getInstances()

        // then
        assertEquals(0, instances.size)
    }

    @Test
    fun `a single query and a single command gets detected`() {
        // given
        val queryMsv = Microservice(name = "query side")
        val cmdMsv = Microservice(name = "command side")
        every { nops.getResults() } returns mapOf(
            queryMsv to Count(
                value = 1, type = "Int", unit = "operations"
            ),
            cmdMsv to Count(
                value = 1, type = "Int", unit = "operations"
            )
        )
        every { nReadOps.getResults() } returns mapOf(queryMsv to Count(
            value = 1, type = "Int", unit = "operations"
        ))
        every { nWriteOps.getResults() } returns mapOf(cmdMsv to Count(
            value = 1, type = "Int", unit = "operations"
        ))
        every { asyncMessaging.getInstances() } returns setOf(
            AsyncMessagingInstance(
                publisher = cmdMsv,
                subscriber = queryMsv
            )
        )

        // when
        underTest.run()
        val instances = underTest.getInstances()

        // then
        assertEquals(1, instances.size)
    }

    @Test
    fun `a single query and a pair of commands gets detected`() {
        // given
        val queryMsv = Microservice(name = "query side")
        val cmd1Msv = Microservice(name = "1st command side")
        val cmd2Msv = Microservice(name = "2nd command side")
        every { nops.getResults() } returns mapOf(
            queryMsv to Count(
                value = 1, type = "Int", unit = "operations"
            ),
            cmd1Msv to Count(
                value = 1, type = "Int", unit = "operations"
            ),
            cmd2Msv to Count(
                value = 1, type = "Int", unit = "operations"
            )
        )
        every { nReadOps.getResults() } returns mapOf(queryMsv to Count(
            value = 1, type = "Int", unit = "operations"
        ))
        every { nWriteOps.getResults() } returns mapOf(
            cmd1Msv to Count(
                value = 1, type = "Int", unit = "operations"
            ),
            cmd2Msv to Count(
                value = 1, type = "Int", unit = "operations"
            )
        )
        every { asyncMessaging.getInstances() } returns setOf(
            AsyncMessagingInstance(
                publisher = cmd1Msv,
                subscriber = queryMsv
            ),
            AsyncMessagingInstance(
                publisher = cmd2Msv,
                subscriber = queryMsv
            )
        )

        // when
        underTest.run()
        val instances = underTest.getInstances()

        // then
        assertEquals(1, instances.size)
        val cqrs = instances.first() as CqrsInstance
        assertEquals(queryMsv, cqrs.query)
        assertEquals(2, cqrs.commands.size)
    }
}