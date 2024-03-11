package com.usvision.analyses.detector

import com.usvision.model.domain.Microservice
import com.usvision.model.domain.Module

class SingleServicePerHostInstance(
    val module: Module,
    val microservice: Microservice
) : ArchitectureInsight {
    companion object {
        fun of(pair: Pair<Module,Microservice>) = SingleServicePerHostInstance(
            module = pair.first,
            microservice = pair.second
        )
    }
}