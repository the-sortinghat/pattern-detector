package com.usvision.analyses.analyzer

import com.usvision.model.visitor.Visitable
import com.usvision.model.visitor.Visitor

interface Analyzer<out T> : Visitor {
    fun getResults(): Map<Visitable, T>
}

