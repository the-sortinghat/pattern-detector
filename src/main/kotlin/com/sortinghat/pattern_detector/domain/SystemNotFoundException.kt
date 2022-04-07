package com.sortinghat.pattern_detector.domain

class SystemNotFoundException(id: Int): Exception("System id=$id not found") {
}