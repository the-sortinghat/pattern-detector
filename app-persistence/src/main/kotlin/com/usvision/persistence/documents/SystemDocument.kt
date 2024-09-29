package com.usvision.persistence.documents

import com.usvision.model.domain.CompanySystem
import com.usvision.model.domain.MessageChannel
import com.usvision.model.domain.Microservice
import com.usvision.model.domain.Module
import com.usvision.model.domain.databases.Database
import com.usvision.model.domain.operations.Operation
import com.usvision.model.domain.operations.RestEndpoint
import com.usvision.model.systemcomposite.System
import com.usvision.persistence.exceptions.UnknownOperationClassException
import com.usvision.persistence.exceptions.UnknownSystemClassException
import org.bson.Document
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class SystemDocument(
    @BsonId val id: ObjectId,
    val name: String,
    val subsystems: Set<SystemDocument>? = null,
    val exposedOperations: Set<Document>? = null,
    val consumedOperations: Set<Document>? = null,
    val databases: Set<DatabaseDocument>? = null,
    val publishedChannels: Set<MessageChannelDocument>? = null,
    val subscribedChannels: Set<MessageChannelDocument>? = null,
    val module: ModuleDocument? = null
)

data class DatabaseDocument(
    @BsonId val id: ObjectId,
    val description: String
)

data class MessageChannelDocument(
    @BsonId val id: ObjectId,
    val name: String
)

data class ModuleDocument(
    @BsonId val id: ObjectId,
    val uuid: String
)

fun CompanySystem.toDocument(
    id: ObjectId = ObjectId()
): SystemDocument = SystemDocument(
    id = id,
    name = this.name,
    subsystems = this.getSubsystemSet().toDocument()
)

fun Microservice.toDocument(
    id: ObjectId = ObjectId()
): SystemDocument = SystemDocument(
    id = id,
    name = this.name,
    module = this.module.toDocument(),
    databases = this.getDatabases().toDocument(),
    exposedOperations = this.getExposedOperations().toDocument(),
    consumedOperations = this.getConsumedOperations().toDocument(),
    publishedChannels = this.getPublishChannels().toDocument(),
    subscribedChannels = this.getSubscribedChannels().toDocument(),
)

private fun System.toDocument(
    id: ObjectId = ObjectId()
) = when (this) {
    is CompanySystem -> {
        this.toDocument(id)
    }
    is Microservice -> {
        this.toDocument(id)
    }
    else -> {
        throw UnknownSystemClassException(this.name)
    }
}

private fun Set<System>.toDocument(): Set<SystemDocument> = this.map { it.toDocument() }.toSet()

private fun Set<Database>.toDocument(): Set<DatabaseDocument> = this.map { it.toDocument() }.toSet()

private fun Set<Operation>.toDocument(): Set<Document>  = this.map { it.toDocument() }.toSet()

fun RestEndpoint.toDocument(): Document {
    return Document().also { doc: Document ->
        doc["description"] = this.description
        doc["httpVerb"] = this.httpVerb
        doc["path"] = this.path
    }
}

private fun Set<MessageChannel>.toDocument(): Set<MessageChannelDocument> = this.map { it.toDocument() }.toSet()

private fun MessageChannel.toDocument(id: ObjectId = ObjectId()) = MessageChannelDocument(id = id, name)

private fun Module.toDocument() = ModuleDocument(id = ObjectId(this.id), uuid = id)

private fun Operation.toDocument() = when (this) {
    is RestEndpoint -> this.toDocument()
    else -> throw UnknownOperationClassException()
}

private fun Database.toDocument() = DatabaseDocument(id = ObjectId(this.id), description = this.description)
