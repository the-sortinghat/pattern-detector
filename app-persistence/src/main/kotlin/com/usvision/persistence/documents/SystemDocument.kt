package com.usvision.persistence.documents

import com.usvision.model.domain.operations.RestEndpoint
import org.bson.Document
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class SystemDocument(
    @BsonId val id: ObjectId,
    val name: String,
    val subsystems: Set<SystemDocument>? = null,
    val exposedOperations: Set<Document>? = null,
    val databases: Set<DatabaseDocument>? = null
    // TODO: add consumed operations
    // TODO: add published channels
    // TODO: add subscribed channels
)

fun RestEndpoint.toDocument(): Document {
    return Document().also { doc: Document ->
        doc["description"] = this.description
        doc["httpVerb"] = this.httpVerb
        doc["path"] = this.path
    }
}

data class DatabaseDocument(
    @BsonId val id: ObjectId,
    val description: String
)