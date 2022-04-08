package com.sortinghat.pattern_detector.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.UUID

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class SystemTest {

    @Test
    fun `System hydrate returns a new instance with the given UUID`() {
        // given
        val name = "test"
        val uuid = UUID.randomUUID()

        // when
        val hydratedSystem = System.hydrate(name, uuid)

        // then
        assertEquals(hydratedSystem.id, uuid)
    }

    @Test
    fun `System create returns a new instance without needing to provide UUID`() {
        // given
        val name = "test"

        // when
        val createdSystem = System.create(name)

        // then
        assertEquals(createdSystem.name, name)
        assertNotNull(createdSystem.id)
    }
}