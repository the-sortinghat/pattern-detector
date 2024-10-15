package com.usvision.model.exceptions

class UnknownOperationClassException(className: String)
    : RuntimeException("Operation has a unknown class to $className")