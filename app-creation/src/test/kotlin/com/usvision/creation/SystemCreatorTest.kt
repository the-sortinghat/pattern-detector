package com.usvision.creation

import com.usvision.model.domain.MessageChannel
import com.usvision.model.domain.databases.PostgreSQL
import com.usvision.model.domain.operations.RestEndpoint
import com.usvision.model.systemcomposite.System
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.*

internal class SystemCreatorTest {
    private val systemAggregateStorage = mockk<SystemAggregateStorage>()
    private val systemCreator = SystemCreator(systemAggregateStorage)

    @Test
    fun `it saves a CompanySystem and returns itself when a fatherName is not passed as argument`() {
        val systemName = "name"
        val companySystem = CompanySystemDTO(systemName)

        every { systemAggregateStorage.getSystem(systemName) } returns null
        every { systemAggregateStorage.save(companySystem) } returns companySystem

        val resultCompanySystem = assertDoesNotThrow {
            systemCreator.createCompanySystem(companySystem)
        }

        verify(exactly = 1) {
            systemAggregateStorage.getSystem(systemName)
            systemAggregateStorage.save(companySystem)
        }
        verify(exactly = 0) {
            systemAggregateStorage.getCompanySystem(any())
            systemAggregateStorage.save(any<MicroserviceDTO>())
        }

        assertEquals(companySystem, resultCompanySystem)
    }

    @Test
    fun `it adds a CompanySystem to the father system and returns it when a CompanySystem and a fatherName are passed as arguments`() {
        val systemName = "name"
        val fatherSystemName = "fatherName"
        val companySystem = CompanySystemDTO(systemName)

        val fatherCompanySystem = CompanySystemDTO(fatherSystemName)

        every { systemAggregateStorage.getSystem(systemName) } returns null
        every { systemAggregateStorage.getCompanySystem(fatherSystemName) } returns fatherCompanySystem
        every { systemAggregateStorage.save(fatherCompanySystem) } returns fatherCompanySystem

        val resultCompanySystem = assertDoesNotThrow {
            systemCreator.createCompanySystem(companySystem, fatherSystemName)
        }

        verify(exactly = 1) {
            systemAggregateStorage.getSystem(systemName)
            systemAggregateStorage.getCompanySystem(fatherSystemName)
            systemAggregateStorage.save(fatherCompanySystem)
        }
        verify(exactly = 0) {
            systemAggregateStorage.save(any<MicroserviceDTO>())
        }

        assertEquals(fatherCompanySystem, resultCompanySystem)
        assertContains(resultCompanySystem.getSubsystemSet(), companySystem)
    }

    @Test
    fun `it throws Exception when CompanySystem and a fatherName are passed as arguments and father System does not exist`() {
        val systemName = "name"
        val fatherSystemName = "fatherName"
        val companySystem = CompanySystemDTO(systemName)

        every { systemAggregateStorage.getSystem(systemName) } returns null
        every { systemAggregateStorage.getCompanySystem(fatherSystemName) } returns null

        assertThrows<Exception> {
            systemCreator.createCompanySystem(companySystem, fatherSystemName)
        }

        verify(exactly = 1) {
            systemAggregateStorage.getSystem(systemName)
            systemAggregateStorage.getCompanySystem(fatherSystemName)
        }
        verify(exactly = 0) {
            systemAggregateStorage.save(any<CompanySystemDTO>())
            systemAggregateStorage.save(any<MicroserviceDTO>())
        }
    }

    @Test
    fun `it throws exception when a system with the same name already exists when attempting to create a CompanySystem`() {
        val systemName = "name"

        every { systemAggregateStorage.getSystem(systemName) } returns mockk<System>()

        assertThrows<Exception> {
            systemCreator.createCompanySystem(CompanySystemDTO(systemName))
        }

        verify(exactly = 1) {
            systemAggregateStorage.getSystem(systemName)
        }
        verify(exactly = 0) {
            systemAggregateStorage.getCompanySystem(any())
            systemAggregateStorage.save(any<CompanySystemDTO>())
            systemAggregateStorage.save(any<MicroserviceDTO>())
        }
    }



    @Test
    fun `it saves a Microservice and returns itself when a fatherName is not passed as argument`() {
        val systemName = "name"
        val microservice = MicroserviceDTO(systemName)

        every { systemAggregateStorage.getSystem(systemName) } returns null
        every { systemAggregateStorage.save(microservice) } returns microservice

        val resultCompanySystem = assertDoesNotThrow {
            systemCreator.createMicroservice(microservice)
        }

        verify(exactly = 1) {
            systemAggregateStorage.getSystem(systemName)
            systemAggregateStorage.save(microservice)
        }
        verify(exactly = 0) {
            systemAggregateStorage.getCompanySystem(any())
            systemAggregateStorage.save(any<CompanySystemDTO>())
        }

        assertEquals(microservice, resultCompanySystem)
    }

    @Test
    fun `it adds a Microservice to the father system and returns it when a Microservice and a fatherName are passed as arguments`() {
        val systemName = "name"
        val fatherSystemName = "fatherName"
        val microservice = MicroserviceDTO(systemName)

        val fatherCompanySystem = CompanySystemDTO(fatherSystemName)

        every { systemAggregateStorage.getSystem(systemName) } returns null
        every { systemAggregateStorage.getCompanySystem(fatherSystemName) } returns fatherCompanySystem
        every { systemAggregateStorage.save(fatherCompanySystem) } returns fatherCompanySystem

        val resultCompanySystem = assertDoesNotThrow {
            systemCreator.createMicroservice(microservice, fatherSystemName)
        }

        verify(exactly = 1) {
            systemAggregateStorage.getSystem(systemName)
            systemAggregateStorage.getCompanySystem(fatherSystemName)
            systemAggregateStorage.save(fatherCompanySystem)
        }
        verify(exactly = 0) {
            systemAggregateStorage.save(any<MicroserviceDTO>())
        }

        assertEquals(fatherCompanySystem, resultCompanySystem)
        assertIs<CompanySystemDTO>(resultCompanySystem)
        assertContains(resultCompanySystem.getSubsystemSet(), microservice)
    }

    @Test
    fun `it throws Exception when Microservice and a fatherName are passed as arguments and father System does not exist`() {
        val systemName = "name"
        val fatherSystemName = "fatherName"
        val microservice = MicroserviceDTO(systemName)

        every { systemAggregateStorage.getSystem(systemName) } returns null
        every { systemAggregateStorage.getCompanySystem(fatherSystemName) } returns null

        assertThrows<Exception> {
            systemCreator.createMicroservice(microservice, fatherSystemName)
        }

        verify(exactly = 1) {
            systemAggregateStorage.getSystem(systemName)
            systemAggregateStorage.getCompanySystem(fatherSystemName)
        }
        verify(exactly = 0) {
            systemAggregateStorage.save(any<CompanySystemDTO>())
            systemAggregateStorage.save(any<MicroserviceDTO>())
        }
    }

    @Test
    fun `it throws exception when a system with the same name already exists when attempting to create a Microservice`() {
        val systemName = "name"

        every { systemAggregateStorage.getSystem(systemName) } returns mockk<System>()

        assertThrows<Exception> {
            systemCreator.createMicroservice(MicroserviceDTO(systemName))
        }

        verify(exactly = 1) {
            systemAggregateStorage.getSystem(systemName)
        }

        verify(exactly = 0) {
            systemAggregateStorage.getCompanySystem(any())
            systemAggregateStorage.save(any<CompanySystemDTO>())
            systemAggregateStorage.save(any<MicroserviceDTO>())
        }
    }

    @Test
    fun `it adds rest endpoints to Microservice and saves it`() {
        val microserviceName = "name"
        val microservice = MicroserviceDTO(microserviceName)

        val consumedRestEndpoints = listOf(
            RestEndpoint(httpVerb = "GET", path = "/test")
        )

        val exposedRestEndpoints = listOf(
            RestEndpoint(httpVerb = "POST", path = "/test")
        )

        every { systemAggregateStorage.getMicroservice(microserviceName) } returns microservice
        every { systemAggregateStorage.save(microservice) } returns microservice

        val addRestEndpointsResult = assertDoesNotThrow {
            systemCreator.addOperationsToMicroservice(
                exposedOperations = exposedRestEndpoints,
                consumedOperations = consumedRestEndpoints,
                microserviceName = microserviceName
            )
        }

        verify(exactly = 1) {
            systemAggregateStorage.getMicroservice(microserviceName)
            systemAggregateStorage.save(microservice)
        }

        verify(exactly = 0) {
            systemAggregateStorage.getCompanySystem(any())
            systemAggregateStorage.save(any<CompanySystemDTO>())
        }

        assertContains(addRestEndpointsResult.getExposedOperations(), exposedRestEndpoints.first())
        assertContains(addRestEndpointsResult.getConsumedOperations(), consumedRestEndpoints.first())

        assertTrue {
            addRestEndpointsResult.getExposedOperations().all {
                operation -> operation != consumedRestEndpoints.first()
            }
        }

        assertTrue {
            addRestEndpointsResult.getConsumedOperations().all {
                operation -> operation != exposedRestEndpoints.first()
            }
        }
    }

    @Test
    fun `it throws exception when attempting to add rest endpoints to non-existent Microservice`() {
        val microserviceName = "name"
        val microservice = MicroserviceDTO(microserviceName)

        val consumedRestEndpoints = listOf(
            RestEndpoint(httpVerb = "GET", path = "/test")
        )

        val exposedRestEndpoints = listOf(
            RestEndpoint(httpVerb = "POST", path = "/test")
        )

        every { systemAggregateStorage.getMicroservice(microserviceName) } returns null

        assertThrows<Exception> {
            systemCreator.addOperationsToMicroservice(
                exposedOperations = exposedRestEndpoints,
                consumedOperations = consumedRestEndpoints,
                microserviceName = microserviceName
            )
        }

        verify(exactly = 1) {
            systemAggregateStorage.getMicroservice(microserviceName)
        }

        verify(exactly = 0) {
            systemAggregateStorage.save(microservice)
            systemAggregateStorage.getCompanySystem(any())
            systemAggregateStorage.save(any<CompanySystemDTO>())
        }
    }

    @Test
    fun `it adds message channels to Microservice and saves it`() {
        val microserviceName = "name"
        val microservice = MicroserviceDTO(microserviceName)

        val publishMessageChannels = listOf(
            MessageChannel(name="publish.test")
        )

        val subscribedMessageChannels = listOf(
            MessageChannel(name="subscribe.test")
        )

        every { systemAggregateStorage.getMicroservice(microserviceName) } returns microservice
        every { systemAggregateStorage.save(microservice) } returns microservice

        val addRestEndpointsResult = assertDoesNotThrow {
            systemCreator.addMessageChannelsToMicroservice(
                publishMessageChannels = publishMessageChannels,
                subscribedMessageChannels = subscribedMessageChannels,
                microserviceName = microserviceName
            )
        }

        verify(exactly = 1) {
            systemAggregateStorage.getMicroservice(microserviceName)
            systemAggregateStorage.save(microservice)
        }

        verify(exactly = 0) {
            systemAggregateStorage.getCompanySystem(any())
            systemAggregateStorage.save(any<CompanySystemDTO>())
        }

        assertContains(addRestEndpointsResult.getPublishChannels(), publishMessageChannels.first())
        assertContains(addRestEndpointsResult.getSubscribedChannels(), subscribedMessageChannels.first())

        assertTrue {
            addRestEndpointsResult.getPublishChannels().all {
                    operation -> operation != subscribedMessageChannels.first()
            }
        }

        assertTrue {
            addRestEndpointsResult.getSubscribedChannels().all {
                    operation -> operation != publishMessageChannels.first()
            }
        }
    }

    @Test
    fun `it throws exception when attempting to add message channels to non-existent Microservice`() {
        val microserviceName = "name"
        val microservice = MicroserviceDTO(microserviceName)

        val publishMessageChannels = listOf(
            MessageChannel(name="publish.test")
        )

        val subscribedMessageChannels = listOf(
            MessageChannel(name="subscribe.test")
        )


        every { systemAggregateStorage.getMicroservice(microserviceName) } returns null


        assertThrows<Exception> {
            systemCreator.addMessageChannelsToMicroservice(
                publishMessageChannels = publishMessageChannels,
                subscribedMessageChannels = subscribedMessageChannels,
                microserviceName = microserviceName
            )
        }

        verify(exactly = 1) {
            systemAggregateStorage.getMicroservice(microserviceName)
        }

        verify(exactly = 0) {
            systemAggregateStorage.save(microservice)
            systemAggregateStorage.getCompanySystem(any())
            systemAggregateStorage.save(any<CompanySystemDTO>())
        }
    }

    @Test
    fun `it adds a database to Microservice and saves it`() {
        val microserviceName = "name"
        val microservice = MicroserviceDTO(microserviceName)

        val database = PostgreSQL()

        every { systemAggregateStorage.getMicroservice(microserviceName) } returns microservice
        every { systemAggregateStorage.save(microservice) } returns microservice


        val addRestEndpointsResult = assertDoesNotThrow {
            systemCreator.addNewDatabaseConnectionToMicroservice(
                database = database,
                microserviceName = microserviceName
            )
        }

        verify(exactly = 1) {
            systemAggregateStorage.getMicroservice(microserviceName)
            systemAggregateStorage.save(microservice)
        }

        verify(exactly = 0) {
            systemAggregateStorage.getCompanySystem(any())
            systemAggregateStorage.save(any<CompanySystemDTO>())
        }

        assertContains(addRestEndpointsResult.getDatabases(), database)
    }

    @Test
    fun `it throws exception when attempting to add a database to non-existent Microservice`() {
        val microserviceName = "name"
        val microservice = MicroserviceDTO(microserviceName)

        val database = PostgreSQL()

        every { systemAggregateStorage.getMicroservice(microserviceName) } returns null

        assertThrows<Exception> {
            systemCreator.addNewDatabaseConnectionToMicroservice(
                database = database,
                microserviceName = microserviceName
            )
        }

        verify(exactly = 1) {
            systemAggregateStorage.getMicroservice(microserviceName)
        }

        verify(exactly = 0) {
            systemAggregateStorage.save(microservice)
            systemAggregateStorage.getCompanySystem(any())
            systemAggregateStorage.save(any<CompanySystemDTO>())
        }
    }
}