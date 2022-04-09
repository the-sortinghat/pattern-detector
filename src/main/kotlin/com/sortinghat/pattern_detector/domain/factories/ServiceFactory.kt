package com.sortinghat.pattern_detector.domain.factories

import com.sortinghat.pattern_detector.domain.model.Service
import com.sortinghat.pattern_detector.domain.SystemRepository
import java.util.*

class ServiceFactory(
    private val systemRepository: SystemRepository
) {

    fun create(name: String, systemId: String): Service {
        val system = systemRepository.findById(UUID.fromString(systemId))

        return Service(name, system)
    }

}