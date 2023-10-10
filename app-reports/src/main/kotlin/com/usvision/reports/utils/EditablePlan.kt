package com.usvision.reports.utils

interface EditablePlan : ExecutablePlan {
    fun addStep(step: Any)
    fun contains(step: Any): Boolean
}