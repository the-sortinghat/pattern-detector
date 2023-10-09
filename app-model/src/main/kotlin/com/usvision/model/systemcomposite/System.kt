package com.usvision.model.systemcomposite

import com.usvision.model.domain.databases.Database
import com.usvision.model.domain.MessageChannel
import com.usvision.model.domain.operations.Operation
import com.usvision.model.visitor.Visitable

interface System : Visitable {
    val name: String

    fun getExposedOperations(): Set<Operation>
    fun getConsumedOperations(): Set<Operation>
    fun getDatabases(): Set<Database>
    fun getPublishChannels(): Set<MessageChannel>
    fun getSubscribedChannels(): Set<MessageChannel>
}
