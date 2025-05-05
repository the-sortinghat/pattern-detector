package com.usvision.web.configuration

import com.usvision.creation.CompanySystemDTO
import com.usvision.creation.SystemCreator
import com.usvision.model.domain.MessageChannel
import com.usvision.model.domain.Microservice
import com.usvision.model.domain.databases.PostgreSQL
import com.usvision.model.domain.operations.RestEndpoint
import com.usvision.reports.ReportSupervisor
import com.usvision.web.dto.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.server.testing.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.plugins.contentnegotiation.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.*

internal class ApplicationRoutingTest {
    private val systemCreator = mockk<SystemCreator>()
    private val reportSupervisor = mockk<ReportSupervisor>()

    @Test
    fun `it creates a new microservice`() = testApplication {
        loadUnitTestModules()
        val client = getHttpClient()

        val microserviceName = "microserviceName"

        val microserviceRequest = SystemRequestDTO(
            name = microserviceName
        )

        val createdMicroservice = Microservice(name = microserviceName)

        val expectedResponseBody = createdMicroservice.toMicroserviceResponseDTO()

        every { systemCreator.createMicroservice(any()) } returns createdMicroservice

        val response = client.postRequest(
            "/microservices",
            microserviceRequest
        )

        val responseBody: SystemResponseDTO = response.body()

        verify(exactly = 1) {
            systemCreator.createMicroservice(createdMicroservice)
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals(expectedResponseBody, responseBody)
    }

    @Test
    fun `it adds a database to a microservice`() = testApplication {
        loadUnitTestModules()
        val client = getHttpClient()

        val microserviceName = "microserviceName"

        val databaseRequest = PostgreSQL()

        val updatedMicroservice = Microservice(
            name = microserviceName
        ).apply {
            addDatabaseConnection(databaseRequest)
        }

        val expectedResponseBody = updatedMicroservice.toMicroserviceResponseDTO()

        every { systemCreator.addNewDatabaseConnectionToMicroservice(any(), any()) } returns updatedMicroservice

        val response = client.postRequest(
            "/microservices/$microserviceName/databases",
            databaseRequest
        )

        val responseBody: MicroserviceResponseDTO = response.body()

        verify(exactly = 1) {
            systemCreator.addNewDatabaseConnectionToMicroservice(databaseRequest, microserviceName)
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals(expectedResponseBody, responseBody)
    }

    @Test
    fun `it adds rest endpoints to a microservice`() = testApplication {
        loadUnitTestModules()
        val client = getHttpClient()

        val microserviceName = "microserviceName"

        val restEndpointsRequest = RestEndpointsRequestDTO(
            consumedOperations = listOf(RestEndpoint("GET", "/test")),
            exposedOperations = listOf(RestEndpoint("POST", "/test"))
        )

        val updatedMicroservice = Microservice(
            name = microserviceName
        ).apply {
            consumeOperation(RestEndpoint("GET", "/test"))
            exposeOperation(RestEndpoint("POST", "/test"))
        }

        val expectedResponseBody = updatedMicroservice.toMicroserviceResponseDTO()

        every { systemCreator.addOperationsToMicroservice(any(), any(), any()) } returns updatedMicroservice

        val response = client.postRequest(
            "/microservices/$microserviceName/rest-endpoints",
            restEndpointsRequest
        )

        val responseBody: MicroserviceResponseDTO = response.body()

        verify(exactly = 1) {
            systemCreator.addOperationsToMicroservice(
                consumedOperations = restEndpointsRequest.consumedOperations,
                exposedOperations = restEndpointsRequest.exposedOperations,
                microserviceName = microserviceName
            )
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals(expectedResponseBody, responseBody)
    }

    @Test
    fun `it adds message channels to a microservice`() = testApplication {
        loadUnitTestModules()
        val client = getHttpClient()

        val microserviceName = "microserviceName"

        val messageChannelsRequest = MessageChannelsRequestDTO(
            publishMessageChannels = listOf(MessageChannel("publish")),
            subscribedMessageChannels = listOf(MessageChannel("subscribe"))
        )

        val updatedMicroservice = Microservice(
            name = microserviceName
        ).apply {
            addPublishChannel(MessageChannel("publish"))
            addSubscribedChannel(MessageChannel("subscribe"))
        }

        val expectedResponseBody = updatedMicroservice.toMicroserviceResponseDTO()

        every { systemCreator.addMessageChannelsToMicroservice(any(), any(), any()) } returns updatedMicroservice

        val response = client.postRequest(
            "/microservices/$microserviceName/message-channels",
            messageChannelsRequest
        )

        val responseBody: MicroserviceResponseDTO = response.body()

        verify(exactly = 1) {
            systemCreator.addMessageChannelsToMicroservice(
                publishMessageChannels = messageChannelsRequest.publishMessageChannels,
                subscribedMessageChannels = messageChannelsRequest.subscribedMessageChannels,
                microserviceName = microserviceName
            )
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals(expectedResponseBody, responseBody)
    }


    @Test
    fun `it creates a new companySystem`() = testApplication {
        loadUnitTestModules()
        val client = getHttpClient()

        val companySystemName = "companySystemName"

        val companySystemRequest = SystemRequestDTO(
            name = companySystemName
        )

        val createdCompanySystem = CompanySystemDTO(name = companySystemName)

        val expectedResponseBody = createdCompanySystem.toCompanySystemResponseDTO()

        every { systemCreator.createCompanySystem(any()) } returns createdCompanySystem

        val response = client.postRequest(
            "/systems",
            companySystemRequest
        )

        val responseBody: CompanySystemResponseDTO = response.body()

        verify(exactly = 1) {
            systemCreator.createCompanySystem(createdCompanySystem)
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals(expectedResponseBody, responseBody)
    }

    @Test
    fun `it adds a child microservice to a CompanySystem`() = testApplication {
        loadUnitTestModules()
        val client = getHttpClient()

        val companySystemName = "companySystemName"
        val microserviceName = "microServiceName"


        val microserviceRequest = SystemRequestDTO(
            name = microserviceName
        )

        val createdMicroservice = Microservice(name = microserviceName)

        val createdCompanySystem = CompanySystemDTO(name = companySystemName).apply {
            addSubsystem(createdMicroservice)
        }

        val expectedResponseBody = createdCompanySystem.toSystemResponseDTO()

        every { systemCreator.createMicroservice(any(), any()) } returns createdCompanySystem

        val response = client.postRequest(
            "/systems/$companySystemName/microservices",
            microserviceRequest
        )

        val responseBody: SystemResponseDTO = response.body()

        verify(exactly = 1) {
            systemCreator.createMicroservice(createdMicroservice, companySystemName)
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals(expectedResponseBody, responseBody)
    }

    @Test
    fun `it adds a child CompanySystem to a CompanySystem`() = testApplication {
        loadUnitTestModules()
        val client = getHttpClient()

        val fatherCompanySystemName = "fatherCompanySystemName"
        val childCompanySystemName = "childCompanySystemName"


        val companySystemRequest = SystemRequestDTO(
            name = childCompanySystemName
        )

        val createdChildCompanySystem = CompanySystemDTO(name = childCompanySystemName)

        val createdFatherCompanySystem = CompanySystemDTO(name = fatherCompanySystemName).apply {
            addSubsystem(createdChildCompanySystem)
        }

        val expectedResponseBody = createdFatherCompanySystem.toSystemResponseDTO()

        every { systemCreator.createCompanySystem(any(), any()) } returns createdFatherCompanySystem

        val response = client.postRequest(
            "/systems/$fatherCompanySystemName/companySubsystems",
            companySystemRequest
        )

        val responseBody: CompanySystemResponseDTO = response.body()

        verify(exactly = 1) {
            systemCreator.createCompanySystem(createdChildCompanySystem, fatherCompanySystemName)
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals(expectedResponseBody, responseBody)
    }


    private suspend inline fun <reified T> HttpClient.postRequest(
        urlString: String,
        body: T
    ) = this.post(urlString) {
        url {
            protocol = URLProtocol.HTTPS
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }

    private fun ApplicationTestBuilder.getHttpClient(): HttpClient {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        return client
    }

    private fun ApplicationTestBuilder.loadUnitTestModules() {
        application {
            configureCORS()
            configureSerialization()
            configureRouting(reportSupervisor, systemCreator)
            configureExceptionHandling()
        }
    }

}