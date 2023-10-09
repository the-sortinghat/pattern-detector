package com.usvision.model.domain.databases

import com.usvision.model.visitor.Visitable

interface Database : Visitable {
    val id: String?
    val description: String
}