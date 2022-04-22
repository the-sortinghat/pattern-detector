package com.sortinghat.pattern_detector.domain.behaviors

interface Visitable {
    fun accept(visitor: Visitor)
}