package com.usvision.web.configuration

import com.usvision.model.domain.MessageChannel

data class MessageChannelsRequestDTO(
    val publishMessageChannels: List<MessageChannel>,
    val subscribedMessageChannels: List<MessageChannel>
)
