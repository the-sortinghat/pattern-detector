package com.usvision.reports.exceptions

class DetectorNotFoundException(
    name: String
) : ReportRequestGenerationException("Detector $name not found")