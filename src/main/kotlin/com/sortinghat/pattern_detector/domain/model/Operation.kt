package com.sortinghat.pattern_detector.domain.model

import com.sortinghat.pattern_detector.domain.behaviors.Visitable
import com.sortinghat.pattern_detector.domain.behaviors.Visitor

@Suppress("unused")
enum class HttpVerb {
    GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD, CONNECT, TRACE
}

data class Operation(
    val verb: HttpVerb,
    val uri: String
) : Visitable {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    override fun children(): Iterable<Visitable> {
        return emptyList()
    }

    override fun toString(): String {
        return "Operation(verb=$verb, uri='$uri')"
    }


}