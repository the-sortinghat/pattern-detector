package com.usvision.creation

import com.usvision.model.systemcomposite.System
import com.usvision.model.systemcomposite.SystemOfSystems

interface SystemRepository {
    fun getSystem(name: String): System?
    fun save(system: System): System
    fun getSystemOfSystems(fatherSystemName: String): SystemOfSystems?
}