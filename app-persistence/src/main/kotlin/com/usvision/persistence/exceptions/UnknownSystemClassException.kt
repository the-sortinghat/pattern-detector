package com.usvision.persistence.exceptions

class UnknownSystemClassException(name: String)
    : RuntimeException("System $name has a unknown class to SystemDocument")
