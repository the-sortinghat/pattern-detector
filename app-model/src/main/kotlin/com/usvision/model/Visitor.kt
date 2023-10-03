package com.usvision.model

interface Visitable {
    fun accept(visitor: Visitor)
}

interface Visitor {
    fun visit(companySystem: CompanySystem)
    fun visit(microservice: Microservice)
}