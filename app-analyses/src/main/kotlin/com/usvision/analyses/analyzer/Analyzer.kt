package com.usvision.analyses.analyzer

import com.usvision.model.Visitable
import com.usvision.model.Visitor

interface Analyzer<out T> : Visitor {
    fun getResults(): Map<Visitable, T>
}

