package com.usvision.analyses.detector

import com.usvision.model.domain.Microservice
import com.usvision.model.domain.databases.Database
import kotlinx.serialization.Serializable

@Serializable
data class SharedPersistenceInstance(
    val database: Database,
    val clientServices: Set<Microservice>
) : ArchitectureInsight {
    companion object {
        fun of(pair: Pair<Database, Set<Microservice>>) = SharedPersistenceInstance(
            database = pair.first,
            clientServices = pair.second
        )
    }
}