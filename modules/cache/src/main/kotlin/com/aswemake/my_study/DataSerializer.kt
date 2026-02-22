package com.aswemake.my_study

import logger
import tools.jackson.module.kotlin.jacksonObjectMapper

object DataSerializer {
    private val objectMapper = jacksonObjectMapper()
    private val log = logger<DataSerializer>()

    fun serializeOrException(data: Any?): String {
        try {
            return objectMapper.writeValueAsString(data)
        } catch (e: Exception) {
            log.error("[DataSerializer.serializeOrException] data={}", data, e)
            throw RuntimeException(e)
        }
    }

    fun <T> deserializeOrNull(data: String, clazz: Class<T>): T? {
        try {
            return objectMapper.readValue(data, clazz)
        } catch (e: Exception) {
            log.error("[DataSerializer.deserializeOrNull] data={}", data, e)
            return null
        }
    }
}