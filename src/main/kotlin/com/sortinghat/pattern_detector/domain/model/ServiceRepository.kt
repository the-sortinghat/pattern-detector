package com.sortinghat.pattern_detector.domain.model

interface ServiceRepository {
    fun findAllOfSystem(id: String): List<Service>
}