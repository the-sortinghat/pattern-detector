package com.sortinghat.pattern_detector.domain.factories

import com.sortinghat.pattern_detector.domain.model.System
import java.util.UUID

class SystemFactory {

    fun create(name: String, id: UUID? = null): System {
        return System(
            name,
            id = id ?: UUID.randomUUID()
        )
    }

}