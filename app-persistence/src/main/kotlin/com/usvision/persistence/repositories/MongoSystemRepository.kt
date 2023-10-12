package com.usvision.persistence.repositories

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.usvision.model.systemcomposite.System
import com.usvision.persistence.documents.SystemDocument
import com.usvision.persistence.exceptions.SystemNotFoundException
import com.usvision.reports.SystemRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.util.NoSuchElementException

class MongoSystemRepository(db: MongoDatabase) : SystemRepository {
    companion object {
        const val COLLECTION_NAME = "systems"
    }

    private val systemCollection: MongoCollection<SystemDocument>

    init {
        systemCollection = db.getCollection<SystemDocument>(COLLECTION_NAME)
    }

    override fun load(name: String): System = try {
        runBlocking {
            systemCollection
                .find(Filters.eq("name", name))
                .limit(1)
                .map { SystemMapper.fromDocument(it) }
                .first()
        }
    } catch (nsee: NoSuchElementException) {
        throw SystemNotFoundException(name)
    }
}
