package com.usvision.model.systembuilder

import com.usvision.model.domain.CompanySystem
import org.junit.jupiter.api.assertThrows
import kotlin.test.*

internal class SystemBuilderTest {
    private lateinit var underTest: SystemBuilder

    @BeforeTest
    fun `create clean, new instance of SystemBuilder`() {
        underTest = SystemBuilder()
    }

    @Test
    fun `it defaults to a company system`() {
        // given
        val name = "test"

        // when
        val result = underTest
            .setName(name)
            .build()

        // then
        assertIs<CompanySystem>(result)
        assertEquals(name, result.name)
    }

    @Test
    fun `opening and closing a subsystem environment gives an empty subsys set`() {
        // given
        val name = "test"

        // when
        val result = underTest
            .setName(name)
            .addSubsystems()
            .endSubsystems()
            .build()

        // then
        assertIs<CompanySystem>(result)
        assertContentEquals(listOf(), result.getSubsystemSet())
    }

    @Test
    fun `closing a subsystem env without having opened one throws SystemBuilderException`() {
        // given
        val name = "test"

        // when ... then
        assertThrows<SystemBuilderException> {
            underTest.endSubsystems()
        }
    }

    @Test
    fun `opening a microservice environment returns microservice builder`() {
        // given
        val name = "test"

        // when
        val environment = underTest
            .setName(name)
            .thatHasMicroservices()

        // then
        assertIs<MicroserviceBuilder>(environment)
    }
}