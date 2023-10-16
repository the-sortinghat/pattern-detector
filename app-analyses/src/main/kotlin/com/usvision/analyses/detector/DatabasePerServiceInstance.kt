package com.usvision.analyses.detector

import com.usvision.model.domain.databases.Database
import com.usvision.model.domain.Microservice
import kotlinx.serialization.Serializable

@Serializable
data class DatabasePerServiceInstance(
    val microservice: Microservice,
    val database: Database
) : ArchitectureInsight {
    companion object {
        fun of(pair: Pair<Microservice, Database>) = DatabasePerServiceInstance(
            microservice = pair.first,
            database = pair.second
        )
    }
}