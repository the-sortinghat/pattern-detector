package com.sortinghat.pattern_detector.domain

import java.util.*

class ServiceFactory(
    private val systemRepository: SystemRepository
) {

    fun create(name: String, systemId: String): Service {
        val system = systemRepository.findById(UUID.fromString(systemId))

        return Service(name, system)
    }

}