package com.usvision.model.systemcomposite

import com.usvision.model.domain.databases.Database
import com.usvision.model.domain.MessageChannel
import com.usvision.model.domain.operations.Operation

interface SystemOfComponents : System {
    fun exposeOperation(operation: Operation)
    fun consumeOperation(operation: Operation)
    fun addDatabaseConnection(database: Database)
    fun addPublishChannel(channel: MessageChannel)
    fun addSubscribedChannel(channel: MessageChannel)
}