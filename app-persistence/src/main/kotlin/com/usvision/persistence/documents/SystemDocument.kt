package com.usvision.persistence.documents

import com.usvision.model.domain.CompanySystem
import com.usvision.model.domain.MessageChannel
import com.usvision.model.domain.Microservice
import com.usvision.model.domain.Module
import com.usvision.model.domain.databases.Database
import com.usvision.model.domain.operations.Operation
import com.usvision.model.domain.operations.RestEndpoint
import com.usvision.model.systemcomposite.System
import com.usvision.model.exceptions.UnknownOperationClassException
import com.usvision.model.exceptions.UnknownSystemClassException
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

fun CompanySystem.toSystemDocument(
    id: ObjectId = ObjectId()
): SystemDocument = SystemDocument(
    id = id,
    name = this.name,
    subsystems = this.getSubsystemSet().toSystemDocumentSet()
)

fun Microservice.toSystemDocument(
    id: ObjectId = ObjectId()
): SystemDocument = SystemDocument(
    id = id,
    name = this.name,
    module = this.module.toModuleDocument(),
    databases = this.getDatabases().toDatabaseDocumentSet(),
    exposedOperations = this.getExposedOperations().toOperationDocumentSet(),
    consumedOperations = this.getConsumedOperations().toOperationDocumentSet(),
    publishedChannels = this.getPublishChannels().toMessageChannelDocumentSet(),
    subscribedChannels = this.getSubscribedChannels().toMessageChannelDocumentSet(),
)

private fun System.toSystemDocument(
    id: ObjectId = ObjectId()
) = when (this) {
    is CompanySystem -> {
        this.toSystemDocument(id)
    }
    is Microservice -> {
        this.toSystemDocument(id)
    }
    else -> {
        throw UnknownSystemClassException(this.name, "SystemDocument")
    }
}

private fun Set<System>.toSystemDocumentSet(): Set<SystemDocument> = this.map { it.toSystemDocument() }.toSet()

private fun Set<Database>.toDatabaseDocumentSet(): Set<DatabaseDocument> = this.map { it.toDatabaseDocument() }.toSet()

private fun Set<Operation>.toOperationDocumentSet(): Set<Document>  = this.map { it.toDocument() }.toSet()

fun RestEndpoint.toDocument(): Document {
    return Document().also { doc: Document ->
        doc["description"] = this.description
        doc["httpVerb"] = this.httpVerb
        doc["path"] = this.path
    }
}

private fun Set<MessageChannel>.toMessageChannelDocumentSet(): Set<MessageChannelDocument> = this.map { it.toMessageChannelDocument() }.toSet()

private fun MessageChannel.toMessageChannelDocument(id: ObjectId = ObjectId()) = MessageChannelDocument(id = id, name)

private fun Module.toModuleDocument() = ModuleDocument(id = ObjectId(), uuid = id)

private fun Operation.toDocument() = when (this) {
    is RestEndpoint -> this.toDocument()
    else -> throw UnknownOperationClassException("SystemDocument")
}

private fun Database.toDatabaseDocument() = DatabaseDocument(
    id = this.id?.let { ObjectId(this.id) } ?: ObjectId(),
    description = this.description
)
