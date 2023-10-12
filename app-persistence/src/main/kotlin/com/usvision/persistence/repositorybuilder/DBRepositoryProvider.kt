package com.usvision.persistence.repositorybuilder

import com.usvision.reports.SystemRepository

interface DBRepositoryProvider : DBConnectionBuilder {
    fun getRepository(): SystemRepository
}
