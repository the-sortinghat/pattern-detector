package com.sortinghat.pattern_detector.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object Subscriptions : IntIdTable() {
    val subscriberId = reference("subscriber_id", Services)
    val channelId = reference("channel_id", MessageChannels)
}