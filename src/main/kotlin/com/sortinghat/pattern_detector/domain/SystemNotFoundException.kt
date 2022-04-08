package com.sortinghat.pattern_detector.domain

import java.util.UUID

class SystemNotFoundException(id: UUID): Exception("System id=$id not found") {
}