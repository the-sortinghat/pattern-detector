package com.sortinghat.pattern_detector.domain.services

import com.sortinghat.pattern_detector.domain.behaviors.Visitable
import com.sortinghat.pattern_detector.domain.model.HttpVerb
import com.sortinghat.pattern_detector.domain.model.Operation
import com.sortinghat.pattern_detector.domain.model.Service
import com.sortinghat.pattern_detector.domain.model.Slug

class Scenarios {
    companion object {
        fun oneServiceOneOperation(): List<Visitable> {
            val service = Service(name = "foo", systemName =  Slug.from("foo service"))
            val operation = Operation(verb = HttpVerb.GET, uri = "/foo")

            service.addOperation(operation)

            return listOf(service)
        }
    }
}