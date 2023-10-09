package com.usvision.model.visitor

import com.usvision.model.domain.CompanySystem
import com.usvision.model.domain.databases.Database
import com.usvision.model.domain.Microservice

interface Visitor {
    fun visit(companySystem: CompanySystem)
    fun visit(microservice: Microservice)
    fun visit(database: Database)
}