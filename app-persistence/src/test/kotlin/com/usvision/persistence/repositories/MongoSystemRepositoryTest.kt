package com.usvision.persistence.repositories

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.usvision.model.domain.CompanySystem
import com.usvision.model.domain.Microservice
import com.usvision.model.domain.operations.RestEndpoint
import com.usvision.persistence.documents.*
import com.usvision.persistence.exceptions.MalformedSystemDocumentException
import com.usvision.persistence.repositorybuilder.MongoDBRepositoryProvider
import com.usvision.persistence.exceptions.SystemNotFoundException
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.bson.types.ObjectId
import org.junit.jupiter.api.assertThrows
import java.util.*
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
}