package com.sortinghat.pattern_detector.db

import com.sortinghat.pattern_detector.db.tables.*
import com.sortinghat.pattern_detector.domain.model.*
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class ServiceRepositoryImpl : ServiceRepository {
    override fun findAllOfSystem(id: String) = transaction {
        val dbIDtoInstance = mutableMapOf<Int, Database>()
        val channelIDtoInstance = mutableMapOf<Int, MessageChannel>()

        Databases
            .selectAll()
            .forEach { db ->
                dbIDtoInstance[db[Databases.id].value] = Database(
                    name = db[Databases.name],
                    type = datasourceFromString(db[Databases.type])
                )
            }

        MessageChannels
            .selectAll()
            .forEach { channel ->
                channelIDtoInstance[channel[MessageChannels.id].value] = MessageChannel(
                    name = channel[MessageChannels.name]
                )
            }

        val serviceIDs: List<Int> = Services
            .select { Services.systemName eq id }
            .map { it[Services.id].value }

        val moduleIDtoInstance = mutableMapOf<Int, Module>()
        serviceIDs.forEach { sid ->
            Services
                .slice(Services.moduleId)
                .select { Services.id eq sid }
                .map { it[Services.moduleId] }
                .toSet()
                .forEach { mid -> moduleIDtoInstance[mid.value] = Module() }
        }

        serviceIDs.map { sid ->
            val ops = Operations
                .select { Operations.exposerId eq sid }
                .map { Operation(
                    verb = httpVerbFromString(it[Operations.verb]),
                    uri = it[Operations.uri]
                ) }
                .toMutableSet()

            val usagePayloads = Services.innerJoin(DatabaseUsages)
                .slice(DatabaseUsages.databaseId, DatabaseUsages.accessMode)
                .select { Services.id eq sid }
                .map {
                    val dbID = it[DatabaseUsages.databaseId].value
                    val db = dbIDtoInstance[dbID]
                    val mode = accessModeFromString(it[DatabaseUsages.accessMode])
                    mapOf(
                        "db" to db,
                        "mode" to mode
                    )
                }

            val channelsPublishes = Publications
                .select { Publications.publisherId eq sid }
                .map { pub -> channelIDtoInstance[pub[Publications.channelId].value]!! }
                .toSet()

            val channelSubscribed = Subscriptions
                .select { Subscriptions.subscriberId eq sid }
                .map { sub -> channelIDtoInstance[sub[Subscriptions.channelId].value]!! }
                .toSet()

            Services
                .select { Services.id eq sid }
                .map {
                    val svc = Service(
                        name = it[Services.name],
                        systemName = Slug.from(it[Services.systemName]),
                        exposedOperations = ops,
                        module = moduleIDtoInstance[it[Services.moduleId].value]!!,
                        channelsPublished = channelsPublishes.toMutableSet(),
                        channelsListening = channelSubscribed.toMutableSet()
                    )

                    usagePayloads.forEach { map ->
                        DatabaseUsage(
                            service = svc,
                            database = map["db"] as Database,
                            accessMode = map["mode"] as DatabaseAccessMode
                        )
                    }

                    svc
                }[0]
        }
    }
}