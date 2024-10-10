package com.usvision.persistence.repositorybuilder

import com.usvision.creation.SystemAggregateStorage
import com.usvision.reports.SystemRepository

interface DBRepositoryProvider : DBConnectionBuilder {
    fun getRepository(): SystemRepository
    fun getAggregateStorage(): SystemAggregateStorage
}
