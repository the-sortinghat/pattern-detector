package com.sortinghat.pattern_detector.domain.model

import com.sortinghat.pattern_detector.domain.behaviors.Visitable
import com.sortinghat.pattern_detector.domain.behaviors.Visitor

class Service : Visitable {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}