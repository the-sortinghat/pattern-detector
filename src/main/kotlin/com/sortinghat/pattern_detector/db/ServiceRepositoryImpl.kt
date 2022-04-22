package com.sortinghat.pattern_detector.db

import com.sortinghat.pattern_detector.domain.model.Service
import com.sortinghat.pattern_detector.domain.model.ServiceRepository

@Suppress("unused")
class ServiceRepositoryImpl : ServiceRepository {
    override fun findAllOfSystem(id: String): List<Service> {
        return emptyList()
    }
}