package com.sortinghat.pattern_detector.domain.model

import com.sortinghat.pattern_detector.domain.behaviors.Measurable
import com.sortinghat.pattern_detector.domain.behaviors.Visitable
import com.sortinghat.pattern_detector.domain.behaviors.Visitor

data class MessageChannel(
    val name: String,
    val bag: MetricBag = MetricBag()
) : Visitable, Measurable by bag {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    override fun children() = emptyList<Visitable>()

    override fun toString(): String {
        return "MessageChannel(name='$name')"
    }


}
