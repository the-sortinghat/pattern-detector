package com.sortinghat.pattern_detector.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object Publications : IntIdTable() {
    val publisherId = reference("publisher_id", Services)
    val channelId = reference("channel_id", MessageChannels)
}