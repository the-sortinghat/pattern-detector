package com.sortinghat.pattern_detector.domain.services

import com.sortinghat.pattern_detector.domain.behaviors.Measurable
import com.sortinghat.pattern_detector.domain.behaviors.Visitable
import com.sortinghat.pattern_detector.domain.model.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class MetricCollectorTest {

    private lateinit var underTest: MetricCollector

    @BeforeEach
    fun setup() {
        underTest = MetricCollector()
    }

    @Test
    fun `it collects n operations correctly for trivial case`() {
        // given
        val visitable = Scenarios.oneServiceOneOperation()

        // when
        visitable.forEach { it.accept(underTest) }

        // then
        assertEquals(1, (visitable[0] as Measurable).get(Metrics.OPERATIONS_OF_SERVICE))
    }

    @Test
    fun `it collects n databases correctly for trivial case`() {
        // given
        val visitable = Scenarios.oneServiceOneDatabase()

        // when
        visitable.forEach { it.accept(underTest) }

        // then
        assertEquals(1, (visitable[0] as Measurable).get(Metrics.DATABASES_USED_BY_SERVICE))
    }

    @Test
    fun `it collects n clients correctly for trivial case`() {
        // given
        val visitable = Scenarios.oneServiceOneDatabase()

        // when
        visitable.forEach { it.accept(underTest) }

        // then
        val db = (visitable[0] as Service).usages.first().database
        assertEquals(1, db.get(Metrics.CLIENTS_OF_DATABASE))
    }

    @Test
    fun `it collects n services per module correctly for trivial case`() {
        // given
        val visitable: List<Visitable> = Scenarios.oneModuleWithOneService()

        // when
        visitable.forEach { it.accept(underTest) }

        // then
        val module = (visitable[0] as Service).module
        assertEquals(1, (module as Measurable).get(Metrics.SERVICES_PER_MODULE))
    }

    @Test
    fun `it collects sync dep correctly for the trivial case`() {
        // given
        val visitable = Scenarios.oneQueryServiceWithTwoSyncDependencies()

        // when
        visitable.forEach { it.accept(underTest) }

        // then
        assertEquals(2, (visitable[0] as Service).get(Metrics.SYNC_DEPENDENCY))
        assertEquals(0, (visitable[1] as Service).get(Metrics.SYNC_DEPENDENCY))
        assertEquals(0, (visitable[2] as Service).get(Metrics.SYNC_DEPENDENCY))
    }

    @Test
    fun `it collects async dep correctly for the trivial case`() {
         // given
        val visitable = Scenarios.onePublisherOneSubscriber()

        // when
        visitable.forEach { it.accept(underTest) }

        // then
        assertEquals(0, (visitable[0] as Service).get(Metrics.ASYNC_DEPENDENCY))
        assertEquals(1, (visitable[1] as Service).get(Metrics.ASYNC_DEPENDENCY))
    }

    @Test
    fun `it collects async imp correctly for the trivial case`() {
        // given
        val visitable = Scenarios.onePublisherOneSubscriber()

        // when
        visitable.forEach { it.accept(underTest) }

        // then
        assertEquals(1, (visitable[0] as Service).get(Metrics.ASYNC_IMPORTANCE))
        assertEquals(0, (visitable[1] as Service).get(Metrics.ASYNC_IMPORTANCE))
    }

    @Test
    fun `it collects async imp and dep correctly for a more complex case`() {
        // given
        val visitable = Scenarios.onePublisherTwoSubscribers()

        // when
        visitable.forEach { it.accept(underTest) }

        // then
        assertEquals(2, (visitable[0] as Service).get(Metrics.ASYNC_IMPORTANCE))
        assertEquals(0, (visitable[1] as Service).get(Metrics.ASYNC_IMPORTANCE))
        assertEquals(0, (visitable[2] as Service).get(Metrics.ASYNC_IMPORTANCE))

        assertEquals(0, (visitable[0] as Service).get(Metrics.ASYNC_DEPENDENCY))
        assertEquals(1, (visitable[1] as Service).get(Metrics.ASYNC_DEPENDENCY))
        assertEquals(1, (visitable[2] as Service).get(Metrics.ASYNC_DEPENDENCY))
    }
}