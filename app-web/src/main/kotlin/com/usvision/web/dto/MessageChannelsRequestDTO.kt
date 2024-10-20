package com.usvision.web.dto

import com.usvision.model.domain.MessageChannel
import kotlinx.serialization.Serializable

@Serializable
data class MessageChannelsRequestDTO(
    val publishMessageChannels: List<MessageChannel>,
    val subscribedMessageChannels: List<MessageChannel>
)
