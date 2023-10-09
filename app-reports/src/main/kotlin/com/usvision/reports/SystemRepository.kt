package com.usvision.reports

import com.usvision.model.systemcomposite.System

interface SystemRepository {
    fun load(name: String): System
}