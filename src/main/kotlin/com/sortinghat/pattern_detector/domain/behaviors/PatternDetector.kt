package com.sortinghat.pattern_detector.domain.behaviors

interface PatternDetector {

    fun getResults(): Set<Pattern>

}