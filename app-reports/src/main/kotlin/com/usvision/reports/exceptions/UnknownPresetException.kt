package com.usvision.reports.exceptions

class UnknownPresetException(name: String)
    : RuntimeException("Preset $name is unknown")