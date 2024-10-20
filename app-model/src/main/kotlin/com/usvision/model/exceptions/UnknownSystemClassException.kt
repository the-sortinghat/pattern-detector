package com.usvision.model.exceptions

class UnknownSystemClassException(name: String, className: String)
    : RuntimeException("System $name has a unknown class to $className")
