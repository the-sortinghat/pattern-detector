package com.usvision.persistence.repositories

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.usvision.creation.SystemAggregateStorage
import com.usvision.model.domain.CompanySystem
import com.usvision.model.domain.Microservice
import com.usvision.model.systemcomposite.System
import com.usvision.persistence.documents.SystemDocument
import com.usvision.persistence.documents.toSystemDocument
import com.usvision.persistence.exceptions.SystemNotFoundException
import com.usvision.reports.SystemRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.bson.types.ObjectId


class MongoSystemRepository(db: MongoDatabase) : SystemRepository {
class MongoSystemRepository(db: MongoDatabase) : SystemRepository, SystemAggregateStorage {
    companion object {
        const val COLLECTION_NAME = "systems"
    }

    private val systemCollection: MongoCollection<SystemDocument>

    init {
        systemCollection = db.getCollection<SystemDocument>(COLLECTION_NAME)
    }


    override fun load(name: String): System = getSystem(name) ?: throw SystemNotFoundException(name)

    override fun getSystem(name: String): System?  = runBlocking {
        systemCollection
            .find(Filters.eq("name", name))
            .firstOrNull()
            ?.let { SystemMapper.fromDocument(it) }
    }


    override fun save(companySystem: CompanySystem): CompanySystem = runBlocking {
        val insertedId = systemCollection.insertOne(
            companySystem.toSystemDocument()
        ).insertedId ?: throw SystemNotFoundException(companySystem.name)

        getSystemById(insertedId.asObjectId().value) as CompanySystem
    }

    override fun save(microservice: Microservice): Microservice = runBlocking {
        val insertedId = systemCollection.insertOne(
            microservice.toSystemDocument()
        ).insertedId ?: throw SystemNotFoundException(microservice.name)

        getSystemById(insertedId.asObjectId().value) as Microservice
    }

    override fun getCompanySystem(name: String): CompanySystem? = try {
        getSystem(name = name) as CompanySystem?
    } catch (ex: ClassCastException) {
        null
    }

    override fun getMicroservice(name: String): Microservice? = try {
        getSystem(name = name) as Microservice?
    } catch (ex: ClassCastException) {
        null
    }

    private fun getSystemById(id: ObjectId): System?  = runBlocking {
        systemCollection
            .find(Filters.eq("_id", id))
            .firstOrNull()
            ?.let { SystemMapper.fromDocument(it) }
    }
}
