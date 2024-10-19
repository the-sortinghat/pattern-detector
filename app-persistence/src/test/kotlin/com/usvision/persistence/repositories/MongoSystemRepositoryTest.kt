package com.usvision.persistence.repositories

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.usvision.model.domain.CompanySystem
import com.usvision.model.domain.MessageChannel
import com.usvision.model.domain.Microservice
import com.usvision.model.domain.databases.PostgreSQL
import com.usvision.model.domain.operations.RestEndpoint
import com.usvision.model.systemcomposite.System
import com.usvision.persistence.documents.*
import com.usvision.persistence.exceptions.MalformedSystemDocumentException
import com.usvision.persistence.repositorybuilder.MongoDBRepositoryProvider
import com.usvision.persistence.exceptions.SystemNotFoundException
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.bson.types.ObjectId
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.*
import java.util.stream.Stream
import kotlin.test.*

internal class MongoSystemRepositoryTest {
    private lateinit var underTest: MongoSystemRepository

    private lateinit var db: MongoDatabase
    private lateinit var systemsCollection: MongoCollection<SystemDocument>

    @BeforeTest
    fun `create clean, new instance of MongoSystemRepository`() {
        val builder = MongoDBRepositoryProvider()
        if (!this::db.isInitialized) {
            db = builder.run {
                connectTo("localhost")
                setPort("27017")
                withCredentials("usvision", "supersecret123")
                setDatabase("systems_test")
                getConnection()
            }
        }

        systemsCollection = db
            .getCollection<SystemDocument>(MongoSystemRepository.COLLECTION_NAME)
        underTest = builder.getRepository()

        runBlocking {
            systemsCollection.deleteMany(Document())
        }
    }

    @Test
    fun `it throws when loading a system that has no subsystem nor module`() {
        // given
        val name = "test"
        createSystemWithoutSubsysNorModule(name)

        // when ... then
        assertThrows<MalformedSystemDocumentException> {
            underTest.load(name)
        }
    }

    @Test
    fun `it returns the system when the given name is found`() {
        // given
        val name = "test"
        createSystemWithName(name)

        // when
        val system = underTest.load(name)

        // then
        assertEquals(name, system.name)
    }

    @Test
    fun `it throws SystemNotFound when no such name is found`() {
        // given
        val name = "non existing"
        deleteSystemWithName(name)

        // when ... then
        assertThrows<SystemNotFoundException> {
            underTest.load(name)
        }
    }

    @Test
    fun `it returns a CompanySystem when there is the name, and it has subsystems`() {
        // given
        val name = "company"
        createSystemWithSubsystems(name = name)

        // when
        val system = underTest.load(name)

        // then
        assertIs<CompanySystem>(system)
    }

    @Test
    fun `it returns a CompanySystem when there is the name, and a Microservice as subsystem`() {
        // given
        val name = "company"
        createSystemWithSubMicroservice(name = name)

        // when
        val system = underTest.load(name)

        // then
        assertIs<CompanySystem>(system)
        assertIs<Microservice>(system.getSubsystemSet().first())
    }

    @Test
    fun `it returns a Microservice when there is the name, and there is no subsystem`() {
        // given
        val name = "test"
        createSystemWithoutSubsystems(name)

        // when
        val system = underTest.load(name)

        // then
        assertIs<Microservice>(system)
    }

    @Test
    fun `it returns a Microservice with its operations`() {
        // given
        val name = "test"
        createMicroserviceWithOperations(name)

        // when
        val system = underTest.load(name)

        // then
        assertIs<Microservice>(system)
        assertTrue { system.getExposedOperations().isNotEmpty() }
        assertTrue { system.getConsumedOperations().isNotEmpty() }
    }

    @Test
    fun `it returns a Microservice with its Databases`() {
        // given
        val name = "test"
        createMicroserviceWithDatabases(name)

        // when
        val system = underTest.load(name)

        // then
        assertIs<Microservice>(system)
        assertTrue { system.getDatabases().isNotEmpty() }
    }

    @Test
    fun `it returns a Microservice with its MessageChannels`() {
        // given
        val name = "test"
        createMicroserviceWithChannels(name)

        // when
        val system = underTest.load(name)

        // then
        assertIs<Microservice>(system)
        assertTrue { system.getPublishChannels().isNotEmpty() }
        assertTrue { system.getSubscribedChannels().isNotEmpty() }
    }

    @Test
    fun `it saves a CompanySystem and returns it`() {
        val companySystem = CompanySystem(
            name = "companySystem"
        ).apply {
            addSubsystem(CompanySystem(name = "childCompanySystem"))
            addSubsystem(Microservice(name = "childMicroservice"))
        }

        val createdSystem = underTest.save(companySystem)

        assertEquals(companySystem, createdSystem)
    }

    @Test
    fun `it saves a Microservice and returns it`() {
        val microservice = Microservice(
            name = "microservice"
        ).apply {
            addDatabaseConnection(PostgreSQL())
            addPublishChannel(MessageChannel("publishChannel"))
            addSubscribedChannel(MessageChannel("subscribeChannel"))
            consumeOperation(RestEndpoint("GET", "/test"))
            exposeOperation(RestEndpoint("POST", "/test"))
        }

        val createdSystem = underTest.save(microservice)

        assertEquals(microservice, createdSystem)
    }

    @Test
    fun `it returns a CompanySystem when it exists`()  {
        val companySystemName = "companySystemName"

        val companySystem = CompanySystem(
            name = companySystemName
        ).apply {
            addSubsystem(CompanySystem(name = "childCompanySystem"))
            addSubsystem(Microservice(name = "childMicroservice"))
        }

        underTest.save(companySystem)

        val returnedSystem = underTest.getCompanySystem(companySystemName)

        assertEquals(companySystem, returnedSystem)
    }

    @Test
    fun `it returns null when no such name is found while trying to get a CompanySystem`() {
        assertNull(underTest.getCompanySystem("companySystemName"))
    }

    @Test
    fun `it returns null when given name is from a Microservice while trying to get a CompanySystem`() {
        val microserviceName = "microserviceName"

        underTest.save(Microservice(name = microserviceName))

        assertNull(underTest.getCompanySystem(microserviceName))
    }

    @Test
    fun `it returns a Microservice when it exists`() {
        val microserviceName = "microserviceName"

        val microservice = Microservice(
            name = microserviceName
        ).apply {
            addDatabaseConnection(PostgreSQL())
            addPublishChannel(MessageChannel("publishChannel"))
            addSubscribedChannel(MessageChannel("subscribeChannel"))
            consumeOperation(RestEndpoint("GET", "/test"))
            exposeOperation(RestEndpoint("POST", "/test"))
        }
        underTest.save(microservice)

        val returnedSystem = underTest.getMicroservice(microserviceName)

        assertEquals(microservice, returnedSystem)
    }

    @Test
    fun `it returns null when no such name is found while trying to get a Microservice`() {
        assertNull(underTest.getMicroservice("microserviceName"))
    }

    @Test
    fun `it returns null when given name is from a CompanySystem while trying to get a Microservice`() {
        val companySystemName = "companySystemName"

        underTest.save(CompanySystem(name = companySystemName))

        assertNull(underTest.getMicroservice(companySystemName))
    }

    @ParameterizedTest
    @MethodSource("systemProvider")
    fun `it returns a System when it exists`(system: System) {
        when (system) {
            is Microservice -> underTest.save(system)
            is CompanySystem -> underTest.save(system)
        }

        assertEquals(system, underTest.getSystem(system.name))
    }

    @Test
    fun `it returns null when no such name is found while trying to get a System`() {
        assertNull(underTest.getSystem("systemName"))
    }

    @Test
    fun `it updates existing CompanySystem`() {
        val companySystem = CompanySystem(
            name = "companySystemName"
        )

        underTest.save(companySystem)

        val updatedCompanySystem = companySystem.copy().apply {
            addSubsystem(Microservice(name = "microserviceName"))
        }

        underTest.save(updatedCompanySystem)

        assertEquals(updatedCompanySystem, underTest.getCompanySystem("companySystemName"))
    }

    @Test
    fun `it updates existing Microservice`() {
        val microservice = Microservice(
            name = "microserviceName"
        )

        underTest.save(microservice)

        val updatedMicroservice = microservice.copy().apply {
            addDatabaseConnection(PostgreSQL())
            addPublishChannel(MessageChannel("publishChannel"))
            addSubscribedChannel(MessageChannel("subscribeChannel"))
            consumeOperation(RestEndpoint("GET", "/test"))
            exposeOperation(RestEndpoint("POST", "/test"))
        }

        underTest.save(updatedMicroservice)

        assertEquals(updatedMicroservice, underTest.getMicroservice("microserviceName"))
    }


    private fun createSystemWithoutSubsysNorModule(name: String) = runBlocking {
        systemsCollection.insertOne(
            SystemDocument(
                id = ObjectId(),
                name = name,
                subsystems = null,
                module = null
            )
        )
    }

    private fun createMicroserviceWithChannels(name: String) = runBlocking {
        systemsCollection.insertOne(
            SystemDocument(
                id = ObjectId(),
                name = name,
                publishedChannels = setOf(
                    MessageChannelDocument(
                        id = ObjectId(),
                        name = "channel-one"
                    )
                ),
                subscribedChannels = setOf(
                    MessageChannelDocument(
                        id = ObjectId(),
                        name = "channel-two"
                    )
                ),
                module = ModuleDocument(
                    id = ObjectId(),
                    uuid = UUID.randomUUID().toString()
                )
            )
        )
    }

    private fun createMicroserviceWithDatabases(name: String) = runBlocking {
        systemsCollection.insertOne(
            SystemDocument(
                id = ObjectId(),
                name = name,
                databases = setOf(
                    DatabaseDocument(
                        id = ObjectId(),
                        description = "anything"
                    )
                ),
                module = ModuleDocument(
                    id = ObjectId(),
                    uuid = UUID.randomUUID().toString()
                )
            )
        )
    }

    private fun createSystemWithSubMicroservice(name: String) = runBlocking {
        systemsCollection.insertOne(
            SystemDocument(
                id = ObjectId(),
                name = name,
                subsystems = setOf(
                    SystemDocument(
                        id = ObjectId(),
                        name = "$name-msvc",
                        subsystems = null,
                        module = ModuleDocument(
                            id = ObjectId(),
                            uuid = UUID.randomUUID().toString()
                        )
                    )
                )
            )
        )
    }

    private fun createMicroserviceWithOperations(name: String) = runBlocking {
        systemsCollection.insertOne(
            SystemDocument(
                id = ObjectId(),
                name = name,
                exposedOperations = setOf(
                    RestEndpoint(
                        httpVerb = "GET", path = "/test",
                        description = "test rest endpoint"
                    ).toDocument()
                ),
                consumedOperations = setOf(
                    RestEndpoint(
                        httpVerb = "GET", path = "/outsourcing",
                        description = "an external endpoint"
                    ).toDocument()
                ),
                module = ModuleDocument(
                    id = ObjectId(),
                    uuid = UUID.randomUUID().toString()
                )
            )
        )
    }

    private fun createSystemWithoutSubsystems(name: String) = runBlocking {
        systemsCollection.insertOne(
            SystemDocument(
                id = ObjectId(),
                name = name,
                subsystems = null,
                module = ModuleDocument(
                    id = ObjectId(),
                    uuid = UUID.randomUUID().toString()
                )
            )
        )
    }

    private fun createSystemWithSubsystems(name: String) = runBlocking {
        systemsCollection.insertOne(
            SystemDocument(
                id = ObjectId(),
                name = name,
                subsystems = emptySet()
            )
        )
    }

    private fun deleteSystemWithName(name: String) = runBlocking {
        systemsCollection.deleteMany(
            Filters.eq("name", name)
        )
    }

    private fun createSystemWithName(name: String) = runBlocking {
        systemsCollection.insertOne(
            SystemDocument(
                id = ObjectId(),
                name = name,
                subsystems = emptySet()
            )
        )
    }

    companion object {
        @JvmStatic
        fun systemProvider(): Stream<Arguments> = Stream.of(
            Arguments.of(Microservice(name = "microserviceName")),
            Arguments.of(CompanySystem(name = "companySystemName"))
        )
    }
}