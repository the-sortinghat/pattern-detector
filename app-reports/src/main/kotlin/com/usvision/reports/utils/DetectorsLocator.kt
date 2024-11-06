package com.usvision.reports.utils

import com.usvision.analyses.detector.Detector

class DetectorsLocator { //Classe responsável por buscar os Detectors
    private val detectors: Set<String?>

    init {
        this.detectors = Detector::class //uso de reflection para buscar implementações
            .sealedSubclasses
            .map { it.qualifiedName }
            .toSet()
    }

    fun getAll(): Map<String,String> = this.detectors
        .fold(emptyMap()) { map, qualifiedName ->
            val simpleName = qualifiedName?.split(".")?.last() ?: return@fold map
            map + mapOf(simpleName to qualifiedName)
        }

    fun getAllSimpleName() = getAll().keys

    fun parseToQualifiedName(simpleName: String) = this.getAll()[simpleName]
}