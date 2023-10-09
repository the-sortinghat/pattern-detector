package com.usvision.reports.exceptions

class ClassIsNotDetectorException(
    name: String
) : ReportRequestGenerationException("Class $name is not Detector")