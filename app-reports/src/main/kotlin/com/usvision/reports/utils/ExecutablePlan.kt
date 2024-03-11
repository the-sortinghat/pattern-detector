package com.usvision.reports.utils

interface ExecutablePlan {
    fun hasNextStep(): Boolean
    fun getNextStep(): Any
    fun size(): Int
}