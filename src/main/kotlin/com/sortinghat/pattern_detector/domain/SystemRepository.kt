package com.sortinghat.pattern_detector.domain

import com.sortinghat.pattern_detector.domain.model.System
import java.util.UUID

interface SystemRepository {

	fun save(system: System): System

	fun findById(id: UUID): System

}