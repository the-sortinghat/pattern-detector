package com.sortinghat.pattern_detector.domain.model.patterns

import com.sortinghat.pattern_detector.domain.behaviors.Pattern
import com.sortinghat.pattern_detector.domain.model.Service
import kotlinx.serialization.Serializable

@Serializable
data class AsyncMessage(
    val publisher: String,
    val subscriber: String
) : Pattern {
    companion object {
        fun from(publisher: Service, subscriber: Service): AsyncMessage {
            return AsyncMessage(publisher = publisher.name, subscriber = subscriber.name)
        }
    }
}
