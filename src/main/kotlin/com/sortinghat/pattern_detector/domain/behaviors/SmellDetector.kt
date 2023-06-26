package com.sortinghat.pattern_detector.domain.behaviors

interface SmellDetector {

    fun getResults(): Set<Smell>

}