package com.sortinghat.pattern_detector.domain.services

import com.sortinghat.pattern_detector.domain.model.Service
import com.sortinghat.pattern_detector.domain.model.Slug
import com.sortinghat.pattern_detector.domain.model.patterns.AsyncMessage
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class CQRSDetectorTest {

    private lateinit var underTest: CQRSDetector

    @Test
    fun `it accepts a set of Async Messages as constructor argument`() {
        // given
        val slug = Slug.from("demo system")
        val asyncMessageOccurrences: Set<AsyncMessage> = setOf(AsyncMessage.from(
            publisher = Service(name = "pub", systemName = slug),
            subscriber = Service(name = "sub", systemName = slug)
        ))

        // when ... then
        assertDoesNotThrow { underTest = CQRSDetector(asyncMessageOccurrences) }
    }

    @Test
    fun `it correctly detects cqrs for the trivial case`() {
        // given
        val (visitable, occurrences) = Scenarios.cqrsBetweenTwoServices()
        visitable.forEach { it.accept(visitor = MetricCollector())}
        underTest = CQRSDetector(occurrences)

        // when
        visitable.forEach { it.accept(visitor = underTest) }

        // then
        val results = underTest.getResults()
        assertEquals(1, results.size)
    }

    @Test
    fun `it correctly detects cqrs for a non-trivial multi-command multi-cqrs case`() {
        // given
        val (visitable, occurrences) = Scenarios.twoCqrsEachNonTrivial()
        visitable.forEach { it.accept(visitor = MetricCollector())}
        underTest = CQRSDetector(occurrences)

        // when
        visitable.forEach { it.accept(visitor = underTest) }

        // then
        val results = underTest.getResults()
        assertEquals(2, results.size)
    }
}