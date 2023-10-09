package com.usvision.model.domain

data class MessageChannel(
    val name: String,
    val id: String? = null
) {
    override fun equals(other: Any?): Boolean {
        return if (other !is MessageChannel) false
        else if (id == null || other.id == null) name == other.name
        else id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}