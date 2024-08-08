package com.usvision.model.systembuilder

import com.usvision.model.domain.CompanySystem
import com.usvision.model.domain.Microservice
import com.usvision.model.domain.databases.PostgreSQL
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.assertThrows
import kotlin.test.*

internal class CompanySystemBuilderTest {
    private lateinit var underTest: CompanySystemBuilder

    @BeforeTest
    fun `create clean, new instance of SystemBuilder`() {
        underTest = CompanySystemBuilder()
    }

    @Test
    fun `it builds an entire system`() {
        // given
        val pingsTopicId = "pings-chan-id"
        val pingsTopicName = "pings"
        val timelinePgId = "pg-one"

        // when
        val system = underTest
            .setName("pingr")
            .addSubsystems()
                .setName("backend")
                .thatHasMicroservices()
                    .oneNamed("pings")
                    .exposingRestEndpoint("POST", "/users/{uid}/pings", "create new ping")
                    .thatPublishesTo(pingsTopicId, pingsTopicName)
                    .and()
                    .anotherNamed("timeline")
                    .exposingRestEndpoint("GET", "/users/{uid}/timeline", "the user's timeline")
                    .thatIsSubscribedTo(pingsTopicId, pingsTopicName)
                    .accessingPostgres(timelinePgId)
                .endMicroservices()
            .endSubsystems()
            .build()

        // then
        assertIs<CompanySystem>(system)
        assertEquals(1, system.getSubsystemSet().size)
        val firstLevelSubsys = system.getSubsystemSet().first()
        assertIs<CompanySystem>(firstLevelSubsys)
        assertEquals(2, firstLevelSubsys.getSubsystemSet().size)
        assertIs<Microservice>(firstLevelSubsys.getSubsystemSet().first())
    }


    @Test
    fun `it builds a system with two subsystems`() {

        // given ... when
        val system = underTest
            .setName("Bank")
            .addSubsystems()
                .setName("Bank subsystem one")
                .thatHasMicroservices()
                    .oneNamed("Balance API")
                    .exposingRestEndpoint("GET", "/account/balance", "account balance")
                .endMicroservices()
            .and()
                .setName("Bank subsystem two")
            .endSubsystems()
            .build()

        // then
        assertIs<CompanySystem>(system)
        assertEquals(2, system.getSubsystemSet().size)
        val firstLevelSubsys = system.getSubsystemSet().first()
        assertIs<CompanySystem>(firstLevelSubsys)
        assertEquals(1, firstLevelSubsys.getSubsystemSet().size)
        assertIs<Microservice>(firstLevelSubsys.getSubsystemSet().first())
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
    fun `it throws SystemBuilderException when endSubsystems without setting a name for the subsystem`() {
        // given
        val name = "test"

        // when
        val result = underTest
            .setName(name)
            .addSubsystems()

        // then
        assertThrows<SystemBuilderException> {
            result.endSubsystems()
        }
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

internal class MicroserviceBuilderTest {
    private lateinit var underTest: MicroserviceBuilder

    @BeforeTest
    fun `create clean, new instance of MicroserviceBuilder`() {
        val parent = spyk(CompanySystemBuilder())

        // when
        underTest = parent
            .thatHasMicroservices()
    }

    @Test
    fun `it throws MicroserviceBuilderException when closing a non-opened microservice environment`() {
        // given nothing
        // when ... then
        assertThrows<SystemBuilderException> {
            underTest.endMicroservices()
        }
    }

    @Test
    fun `it returns its parent when closing a properly opened microservice env`() {
        // given
        val parent = spyk(CompanySystemBuilder())
        underTest = parent
            .thatHasMicroservices()

        // when
        val environment = underTest
            .named("something")
            .endMicroservices()

        // then
        assertIs<CompanySystemBuilder>(environment)
        assertEquals(parent, environment)
        verify { parent.addMicroservice(any()) }
    }

    @Test
    fun `it defaults to a new module and a name`() {
        // given
        val name = "micro"

        // when
        val result = underTest
            .named(name)
            .build()

        // then
        assertIs<Microservice>(result)
        assertEquals(name, result.name)
        assertNotNull(result.module)
    }

    @Test
    fun `it throws SystemBuilderException when building a Microservice with no name`() {
        // given nothing
        // when ... then
        assertThrows<SystemBuilderException> {
            underTest.build()
        }
    }

    @Test
    fun `it adds exposed rest endpoints`() {
        // given
        val name = "test"

        // when
        val result = underTest
            .named(name)
            .exposingRestEndpoint(
                httpVerb = "GET", path = "/path",
                description = "test"
            )
            .build()

        // then
        assertIs<Microservice>(result)
        assertEquals(1, result.getExposedOperations().size)
    }

    @Test
    fun `it adds consumed rest endpoints`() {
        // given
        val name = "micro"

        // when
        val result = underTest
            .named(name)
            .thatRequestsHttpEndpoint(
                httpVerb = "GET", path = "/path",
                description = "test"
            )
            .build()

        // then
        assertIs<Microservice>(result)
        assertEquals(1, result.getConsumedOperations().size)
    }

    @Test
    fun `it adds database`() {
        // given
        val name = "micro"
        val dbId = "database-id"

        // when
        val result = underTest
            .named(name)
            .accessingPostgres(id = dbId)
            .build()

        // then
        assertIs<Microservice>(result)
        assertIs<PostgreSQL>(result.getDatabases().first())
    }

    @Test
    fun `it adds published channels`() {
        // given
        val name = "microtest"
        val channelName = "channel"
        val channelId = "channel-id"

        // when
        val result = underTest
            .named(name)
            .thatPublishesTo(
                channelId = channelId,
                channelName = channelName
            )
            .build()

        // then
        assertIs<Microservice>(result)
        assertEquals(channelId, result.getPublishChannels().first().id)
    }

    @Test
    fun `it adds subscribed channels`() {
        // given
        val name = "microtest"
        val channelName = "channel"
        val channelId = "channel-id"

        // when
        val result = underTest
            .named(name)
            .thatIsSubscribedTo(
                channelId = channelId,
                channelName = channelName
            )
            .build()

        // then
        assertIs<Microservice>(result)
        assertEquals(channelId, result.getSubscribedChannels().first().id)
    }

    @Test
    fun `it offers 'oneNamed' as an alias method for named`() {
        // given
        val name = "micro"
        underTest = spyk(underTest)

        // when
        val result = underTest
            .oneNamed(name)
            .build()

        // then
        assertIs<Microservice>(result)
        assertEquals(name, result.name)
        verify { underTest.named(name) }
    }

    @Test
    fun `it offers 'anotherNamed' as an alias method for named`() {
        // given
        val name = "micro"
        underTest = spyk(underTest)

        // when
        val result = underTest
            .anotherNamed(name)
            .build()

        // then
        assertIs<Microservice>(result)
        assertEquals(name, result.name)
        verify { underTest.named(name) }
    }

    @Test
    fun `it throws SystemBuilderException when calling 'named' method twice`() {
        // given
        val name = "micro"

        // when
        val result = underTest
            .named(name)


         //then
        assertThrows<SystemBuilderException> {
            result.named(name)
        }
    }


    @Test
    fun `it throws SystemBuilderException when calling 'and' method twice without setting a name for the second microservice`() {
        // given
        val name = "micro"

        // when
        val result = underTest
            .named(name)
            .and()


        //then
        assertThrows<SystemBuilderException> {
            result.and()
        }
    }

    @Test
    fun `it offers 'and' as a convenience for finishing one and beginning another`() {
        // given
        val nameOne = "name one"
        val nameTwo = "name two"
        val parent = spyk(CompanySystemBuilder())

        // when
        parent
            .thatHasMicroservices()
                .oneNamed(nameOne)
                .and()
                .anotherNamed(nameTwo)
            .endMicroservices()

        // then
        verify(exactly = 2) { parent.addMicroservice(any()) }
    }
}