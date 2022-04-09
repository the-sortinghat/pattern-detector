package com.sortinghat.pattern_detector.domain.factories

import com.sortinghat.pattern_detector.domain.SystemNotFoundException
import com.sortinghat.pattern_detector.domain.SystemRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
internal class ServiceFactoryTest {
    @MockK
    lateinit var systemRepository: SystemRepository

    private val systemFactory = SystemFactory()

    private val underTest: ServiceFactory

    init {
        MockKAnnotations.init(this)
        underTest = ServiceFactory(systemRepository)
    }

    @Test
    fun `ServiceFactory create throws SystemNotFound when there is no such system id`() {
        // given
        val slot = slot<UUID>()
        every {
            systemRepository.findById(id = capture(slot))
        } answers {
            throw SystemNotFoundException(slot.captured)
        }

        // when & then
        val id: String = UUID.randomUUID().toString()
        assertThrows<SystemNotFoundException> {
            underTest.create("any name", id)
        }
    }

    @Test
    fun `ServiceFactory create returns a new service within the given system`() {
        // given
        val system = systemFactory.create("test")
        every { systemRepository.findById(any()) } returns system

        // when
        val name = "test service"
        val service = underTest.create(name, system.id.toString())

        // then
        assertEquals(service.name, name)
        assertEquals(service.system, system)
    }
}