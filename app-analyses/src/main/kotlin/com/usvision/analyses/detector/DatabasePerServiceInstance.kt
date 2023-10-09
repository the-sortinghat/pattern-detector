package com.usvision.analyses.detector

import com.usvision.model.Database
import com.usvision.model.Microservice

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