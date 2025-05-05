package com.usvision.model.exceptions

class UnknownDatabaseClassException(className: String)
    : RuntimeException("Database has a unknown class to $className")
