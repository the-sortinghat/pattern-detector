package com.usvision.analyses.detector

import com.usvision.analyses.analyzer.AsyncDependenciesOfMicroservice
import com.usvision.analyses.analyzer.Relationship
import com.usvision.analyses.analyzer.SyncDependenciesOfMicroservice
import com.usvision.model.domain.Microservice
import com.usvision.model.visitor.Visitable
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CyclicDependencyTest {
    companion object {
        private const val MIN: Int = 3
        private const val MAX: Int = 10
    }

    private lateinit var underTest: CyclicDependency

    @MockK
    private lateinit var mockSync: SyncDependenciesOfMicroservice

    @MockK
    private lateinit var mockAsync: AsyncDependenciesOfMicroservice

    @BeforeTest
    fun `create clean, new instance of CyclicDependency`() {
        MockKAnnotations.init(this)
        underTest = CyclicDependency(
            syncDependenciesOfMicroservice = mockSync,
            asyncDependenciesOfMicroservice = mockAsync
        )
    }

    private fun getRandomNumber() = ((MAX - MIN) * Math.random() + MIN).toInt()

    private fun getRandomBoolean() = Math.random() > 0.5

    @Test
    fun `it handles Microservice A not being a parent and generating null`() {
        // given
        val A = Microservice(name = "A")
        val B = Microservice(name = "B")
        val C = Microservice(name = "C")

        every { mockSync.getResults() } returns mapOf(
            B to setOf(Relationship(with = A)),
            C to setOf(Relationship(with = A))
        )
        every { mockAsync.getResults() } returns emptyMap()

        // when
        underTest.run()
        val instances = underTest.getInstances()

        // then
        assertTrue(instances.isEmpty())
    }

    @Test
    fun `it detects A sync B`() {
        // given
        val A = Microservice(name = "A")
        val B = Microservice(name = "B")
        every { mockSync.getResults() } returns mapOf(
            A to setOf(Relationship(with = B)),
            B to setOf(Relationship(with = A))
        )
        every { mockAsync.getResults() } returns emptyMap()

        // when
        underTest.run()
        val instances = underTest.getInstances()

        // then
        assertEquals(1, instances.size)
    }

    @Test
    fun `it detects A async B`() {
        // given
        val A = Microservice(name = "A")
        val B = Microservice(name = "B")
        every { mockSync.getResults() } returns emptyMap()
        every { mockAsync.getResults() } returns mapOf(
            A to setOf(Relationship(with = B)),
            B to setOf(Relationship(with = A))
        )

        // when
        underTest.run()
        val instances = underTest.getInstances()

        // then
        assertEquals(1, instances.size)
    }

    @Test
    fun `it detects A sync B, B sync C and C sync A`() {
        // given
        val A = Microservice(name = "A")
        val B = Microservice(name = "B")
        val C = Microservice(name = "C")
        every { mockSync.getResults() } returns mapOf(
            A to setOf(Relationship(with = B)),
            B to setOf(Relationship(with = C)),
            C to setOf(Relationship(with = A))
        )
        every { mockAsync.getResults() } returns emptyMap()

        // when
        underTest.run()
        val instances = underTest.getInstances()

        // then
        assertEquals(1, instances.size)
    }

    @Test
    fun `it detects two instances - first A sync B, B sync C and C sync A __ second A sync D`() {
        // given
        val A = Microservice(name = "A")
        val B = Microservice(name = "B")
        val C = Microservice(name = "C")
        val D = Microservice(name = "D")
        every { mockSync.getResults() } returns mapOf(
            A to setOf(Relationship(with = B), Relationship(with = D)),
            B to setOf(Relationship(with = C)),
            C to setOf(Relationship(with = A)),
            D to setOf(Relationship(with = A))
        )
        every { mockAsync.getResults() } returns emptyMap()

        // when
        underTest.run()
        val instances = underTest.getInstances()

        // then
        assertEquals(2, instances.size)
    }

    @Test
    fun `it detects A sync-to B, B async-to A`() {
        // given
        val A = Microservice(name = "A")
        val B = Microservice(name = "B")
        every { mockSync.getResults() } returns mapOf(
            A to setOf(Relationship(with = B))
        )
        every { mockAsync.getResults() } returns mapOf(
            B to setOf(Relationship(with = A))
        )

        // when
        underTest.run()
        val instances = underTest.getInstances()

        // then
        assertEquals(1, instances.size)
    }

    @Test
    fun `it detects sync Bi-B(i + 1), i between 1 and N`() {
        // given
        val N = getRandomNumber()
        val Bi = mutableListOf<Microservice>()
        val syncResults = mutableMapOf<Visitable, Set<Relationship>>()

        for (i in MIN..N) {
            val micro = Microservice(name = "B$i")
            Bi.add(micro)
        }

        syncResults[Bi.first()] = setOf(Relationship(with = Bi.last()))
        Bi.forEachIndexed { i, microservice ->
            if (i == 0) return@forEachIndexed

            val prior = Bi[i - 1]
            syncResults[microservice] = setOf(Relationship(with = prior))
        }

        every { mockSync.getResults() } returns syncResults
        every { mockAsync.getResults() } returns emptyMap()

        // when
        underTest.run()
        val instances = underTest.getInstances()

        // then
        assertEquals(1, instances.size)
    }

    @Test
    fun `it detects async Bi-B(i + 1), i between 1 and N`() {
        // given
        val N = getRandomNumber()
        val Bi = mutableListOf<Microservice>()
        val asyncResults = mutableMapOf<Visitable, Set<Relationship>>()

        for (i in MIN..N) {
            val micro = Microservice(name = "B$i")
            Bi.add(micro)
        }

        asyncResults[Bi.first()] = setOf(Relationship(with = Bi.last()))
        Bi.forEachIndexed { i, microservice ->
            if (i == 0) return@forEachIndexed

            val prior = Bi[i - 1]
            asyncResults[microservice] = setOf(Relationship(with = prior))
        }

        every { mockSync.getResults() } returns emptyMap()
        every { mockAsync.getResults() } returns asyncResults

        // when
        underTest.run()
        val instances = underTest.getInstances()

        // then
        assertEquals(1, instances.size)
    }

    @Test
    fun `it detects mixed Bi-B(i + 1), i between 1 and N`() {
        // given
        val N = getRandomNumber()
        val Bi = mutableListOf<Microservice>()
        val syncResults = mutableMapOf<Visitable, Set<Relationship>>()
        val asyncResults = mutableMapOf<Visitable, Set<Relationship>>()

        for (i in MIN..N) {
            val micro = Microservice(name = "B$i")
            Bi.add(micro)
        }

        val firstIsSync = getRandomBoolean()
        if (firstIsSync)
            syncResults[Bi.first()] = setOf(Relationship(with = Bi.last()))
        else
            asyncResults[Bi.first()] = setOf(Relationship(with = Bi.last()))

        Bi.forEachIndexed { i, microservice ->
            if (i == 0) return@forEachIndexed

            val prior = Bi[i - 1]
            val isSync = getRandomBoolean()
            if (isSync)
                syncResults[microservice] = setOf(Relationship(with = prior))
            else
                asyncResults[microservice] = setOf(Relationship(with = prior))
        }

        every { mockSync.getResults() } returns syncResults
        every { mockAsync.getResults() } returns asyncResults

        // when
        underTest.run()
        val instances = underTest.getInstances()

        // then
        assertEquals(1, instances.size)
    }

    @Test
    fun `it does not detect when A-B, B-C but no C-A`() {
        // given
        val A = Microservice(name = "A")
        val B = Microservice(name = "B")
        val C = Microservice(name = "C")
        every { mockAsync.getResults() } returns mapOf(
            A to setOf(Relationship(with = B)),
            B to setOf(Relationship(with = C))
        )
        every { mockSync.getResults() } returns emptyMap()

        // when
        underTest.run()
        val instances = underTest.getInstances()

        // then
        assertEquals(0, instances.size)
    }
}