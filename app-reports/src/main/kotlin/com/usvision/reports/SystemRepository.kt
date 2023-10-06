package com.usvision.reports

import com.usvision.model.System

interface SystemRepository {
    fun load(name: String): System
}