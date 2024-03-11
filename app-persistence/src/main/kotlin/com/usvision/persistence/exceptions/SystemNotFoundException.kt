package com.usvision.persistence.exceptions

class SystemNotFoundException(name: String)
    : RuntimeException("System $name not found")