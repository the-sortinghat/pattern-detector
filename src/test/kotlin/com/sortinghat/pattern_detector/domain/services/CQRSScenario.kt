package com.sortinghat.pattern_detector.domain.services

import com.sortinghat.pattern_detector.domain.behaviors.Visitable
import com.sortinghat.pattern_detector.domain.model.patterns.AsyncMessage

data class CQRSScenario(
    val visitable: List<Visitable>,
    val occurrences: Set<AsyncMessage>
)
