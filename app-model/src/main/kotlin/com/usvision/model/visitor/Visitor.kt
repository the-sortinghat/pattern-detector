package com.usvision.model.visitor

import com.usvision.model.domain.CompanySystem
import com.usvision.model.domain.databases.Database
import com.usvision.model.domain.Microservice
import com.usvision.model.domain.Module

abstract class Visitor {
    open fun visit(companySystem: CompanySystem) {}
    open fun visit(microservice: Microservice) {}
    open fun visit(database: Database) {}
    open fun visit(module: Module) {}
}