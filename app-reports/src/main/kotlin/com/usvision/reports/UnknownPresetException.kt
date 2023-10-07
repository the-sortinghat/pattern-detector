package com.usvision.reports

class UnknownPresetException(name: String)
    : RuntimeException("Preset $name is unknown")