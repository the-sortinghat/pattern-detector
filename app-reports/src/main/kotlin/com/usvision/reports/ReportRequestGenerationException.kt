package com.usvision.reports

open class ReportRequestGenerationException(
    message: String
) : RuntimeException(message)

class DetectorNotFoundException(
    name: String
) : ReportRequestGenerationException("Detector $name not found")

class ClassIsNotDetectorException(
    name: String
) : ReportRequestGenerationException("Class $name is not Detector")