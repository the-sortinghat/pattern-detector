package com.sortinghat.pattern_detector.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class SystemFactoryTest {
    private val underTest = SystemFactory()

    @Test
    fun `SystemFactory create returns a new instance with ID when no ID is given`() {
        // given
        val name = "test"

        // when
        val system = underTest.create(name)

        // then
        assertNotNull(system.id)
        assertEquals(system.name, name)
    }

    @Test
    fun `SystemFactory create returns a new instance with the given ID when one is provided`() {
        // given
        val name = "test"
        val id = UUID.randomUUID()

        // when
        val system = underTest.create(name, id)

        // then
        assertEquals(system.id, id)
        assertEquals(system.name, name)
    }
}