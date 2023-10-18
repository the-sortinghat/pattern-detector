package com.usvision.persistence.exceptions

class MalformedSystemDocumentException(name: String)
    : RuntimeException("System $name is malformed -- no subsystem nor module")