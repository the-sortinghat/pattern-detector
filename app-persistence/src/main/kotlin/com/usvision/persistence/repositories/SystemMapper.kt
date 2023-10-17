package com.usvision.persistence.repositories

import com.usvision.model.domain.CompanySystem
import com.usvision.model.domain.MessageChannel
import com.usvision.model.domain.Microservice
import com.usvision.model.domain.databases.Database
import com.usvision.model.domain.databases.PostgreSQL
import com.usvision.model.domain.operations.Operation
import com.usvision.model.domain.operations.RestEndpoint
import com.usvision.model.systemcomposite.System
import com.usvision.persistence.documents.DatabaseDocument
import com.usvision.persistence.documents.MessageChannelDocument
import com.usvision.persistence.documents.SystemDocument
import org.bson.Document

class SystemMapper {
    companion object {
        fun fromDocument(systemDocument: SystemDocument): System {
            val hasSubsystems = systemDocument.subsystems != null

            return when (hasSubsystems) {
                true -> createCompanySystem(systemDocument)
                else -> createMicroservice(systemDocument)
            }
        }

        private fun createMicroservice(systemDocument: SystemDocument): Microservice {
            return Microservice(
                name = systemDocument.name
            ).also { svc ->
                systemDocument.exposedOperations?.forEach { doc ->
                    createOperation(doc).also { op -> svc.exposeOperation(op) }
                }
                systemDocument.consumedOperations?.forEach { doc ->
                    createOperation(doc).also { op -> svc.consumeOperation(op)}
                }
                systemDocument.databases?.forEach { dbDoc ->
                    createDatabase(dbDoc).also { db -> svc.addDatabaseConnection(db) }
                }
                systemDocument.publishedChannels?.forEach { channelDoc ->
                    createChannel(channelDoc).also { channel -> svc.addPublishChannel(channel) }
                }
                systemDocument.subscribedChannels?.forEach { channelDoc ->
                    createChannel(channelDoc).also { channel -> svc.addSubscribedChannel(channel) }
                }
            }
        }

        private fun createChannel(channelDoc: MessageChannelDocument): MessageChannel {
            return MessageChannel(
                name = channelDoc.name,
                id = channelDoc.id.toString()
            )
        }

        private fun createDatabase(dbDoc: DatabaseDocument): Database {
            return PostgreSQL(description = dbDoc.description, id = dbDoc.id.toString())
        }

        private fun createOperation(doc: Document): Operation {
            return RestEndpoint(
                httpVerb = doc["httpVerb"] as String,
                path = doc["path"] as String,
                description = doc["description"] as String
            )
        }

        private fun createCompanySystem(systemDocument: SystemDocument): CompanySystem {
            val companySystem = CompanySystem(
                name = systemDocument.name,
            )
            systemDocument.subsystems!!
                .map { fromDocument(it) }
                .forEach { companySystem.addSubsystem(it) }

            return companySystem
        }
    }
}