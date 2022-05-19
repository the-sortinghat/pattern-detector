package com.sortinghat.pattern_detector.domain.model

class Slug internal constructor(val value: String) {
    companion object {
        fun from(text: String): Slug {
            val raw = text.replace(Regex("""\w"""), "_")
            return Slug(raw)
        }
    }

    override fun toString(): String {
        return value
    }


}