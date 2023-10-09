package com.usvision.model.visitor

interface Visitable {
    fun accept(visitor: Visitor)
}