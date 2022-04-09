package com.sortinghat.pattern_detector.domain.model

import java.util.*

data class System(
	val name: String,
	val id: UUID
) {
	private val services = mutableSetOf<Service>()

	fun addService(service: Service) {
		services.add(service)
	}

	fun getServices(): Set<Service> {
		return services
	}
}