package com.usvision.analyses.analyzer

data class Count(
    override val value: Any,
    override val type: String,
    override val unit: String
): Measure