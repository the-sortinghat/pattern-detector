package com.usvision.kafka

import org.apache.kafka.clients.consumer.KafkaConsumer
import java.time.Duration


fun main() {
    val consumerConfigs =
        mapOf(
            "bootstrap.servers" to "localhost:9092",
            "auto.offset.reset" to "earliest",
            "key.deserializer" to "org.apache.kafka.common.serialization.StringDeserializer",
            "value.deserializer" to "org.apache.kafka.common.serialization.ByteArrayDeserializer",
            "group.id" to "usvision.serviceschema",
            "security.protocol" to "PLAINTEXT"
        )

    val topic = "sorting-hat-database.serviceSchema"
    val consumer = KafkaConsumer<String, ByteArray>(consumerConfigs)

    consumer.subscribe(listOf(topic))

    while (true) {
        try {
            consumer.poll(Duration.ofMillis(400)).forEach {
                    item -> println(item.value())
            }
        } catch (ex: Exception) {
            println(ex)
        }
    }



}