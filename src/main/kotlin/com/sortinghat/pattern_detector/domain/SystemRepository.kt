package com.sortinghat.pattern_detector.domain

interface SystemRepository {

	fun save(system: System): System

}