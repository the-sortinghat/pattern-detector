package com.sortinghat.pattern_detector.domain.services

import com.sortinghat.pattern_detector.domain.behaviors.Measurable
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
}