package com.usvision.analyses.analyzer

import com.usvision.model.visitor.Visitable
import com.usvision.model.visitor.Visitor

abstract class Analyzer<out T> : Visitor() {
    abstract fun getResults(): Map<Visitable, T>
}

