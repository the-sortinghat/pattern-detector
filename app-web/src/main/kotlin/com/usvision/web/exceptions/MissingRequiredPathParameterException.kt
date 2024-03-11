package com.usvision.web.exceptions

class MissingRequiredPathParameterException(
    paramName: String, paramType: String
) : RuntimeException(
    "Request missing path parameter - $paramName of type $paramType"
)