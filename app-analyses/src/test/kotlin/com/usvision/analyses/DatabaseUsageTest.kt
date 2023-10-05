package com.usvision.analyses

import com.usvision.model.CompanySystem
import com.usvision.model.Microservice
import com.usvision.model.PostgreSQL
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


internal class DatabaseUsageTest {
    private lateinit var underTest: DatabaseUsage

    @BeforeTest
    fun `create clean, new instance of DatabaseUsage`() {
        underTest = DatabaseUsage()
    }

    @Test
    fun `it connects a single db to a single ms`() {
        // given
        val ms = Microservice(name = "test")
        val db = PostgreSQL(id = "pg")
        ms.addDatabaseConnection(db)

        // when
        ms.accept(underTest)
        val results = underTest.getResults()

        // then
        assertTrue { db in results }
        assertEquals( results[db]!!.size, 1)
        assertTrue { Relationship(with = ms) in results[db]!! }
    }

    @Test
    fun `it connects multiple ms to a single db`() {
        // given
        val sys = CompanySystem(name = "test")
        val ms1 = Microservice(name = "one")
        val ms2 = Microservice(name = "two")
        val db = PostgreSQL(id = "pg")
        ms1.addDatabaseConnection(db)
        ms2.addDatabaseConnection(db)
        sys.addSubsystem(ms1)
        sys.addSubsystem(ms2)

        // when
        sys.accept(underTest)
        val results = underTest.getResults()

        // then
        assertTrue { db in results }
        assertEquals(results[db]!!.size, 2)
        assertTrue { Relationship(with = ms1) in results[db]!! }
        assertTrue { Relationship(with = ms2) in results[db]!! }
    }

    @Test
    fun `it connects a single ms to multiple dbs`() {
        // given
        val ms = Microservice(name = "test")
        val db1 = PostgreSQL(id = "one")
        val db2 = PostgreSQL(id = "two")
        ms.addDatabaseConnection(db1)
        ms.addDatabaseConnection(db2)

        // when
        ms.accept(underTest)
        val results = underTest.getResults()

        // then
        val relationWithMS = Relationship(with = ms)

        assertTrue { db1 in results }
        assertEquals(results[db1]!!.size, 1)
        assertTrue { relationWithMS in results[db1]!! }

        assertTrue { db2 in results }
        assertEquals(results[db2]!!.size, 1)
        assertTrue { relationWithMS in results[db2]!! }
    }
}