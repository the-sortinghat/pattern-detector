package com.usvision.creation

import com.usvision.model.domain.CompanySystem
import com.usvision.model.domain.Microservice
import com.usvision.model.systemcomposite.System

interface SystemAggregateStorage {
    fun getSystem(name: String): System?
    fun save(microservice: Microservice): Microservice
    fun save(companySystem: CompanySystem): CompanySystem
    fun getCompanySystem(name: String): CompanySystem?
    fun getMicroservice(name: String): Microservice?
}