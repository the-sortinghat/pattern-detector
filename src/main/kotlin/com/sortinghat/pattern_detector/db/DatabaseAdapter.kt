package com.sortinghat.pattern_detector.db

import org.jetbrains.exposed.sql.Database

interface DatabaseAdapter {
	fun connect(): Database
}