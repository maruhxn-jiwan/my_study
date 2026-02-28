package com.aswemake.my_study.repository

import com.aswemake.my_study.DataSerializer
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import logger
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

/**
 * 애노테이션을 쓰지 않은 이유: fallbackSupplier를 직접 받아서 처리하기 위함
 */
@Component
class StringRedisRepository(
    private val redisTemplate: StringRedisTemplate,
    private val redisCircuitBreaker: CircuitBreaker,
) {
    private val log = logger<StringRedisRepository>()

    fun <T> get(key: String, clazz: Class<T>, fallbackSupplier: () -> T?): T? =
        execute("get", key, fallbackSupplier) {
            redisTemplate.opsForValue().get(key)?.let { DataSerializer.deserializeOrNull(it, clazz) }
        }

    fun set(key: String, value: Any, ttl: Duration, fallbackSupplier: () -> Unit = {}) =
        execute("set", key, fallbackSupplier) {
            redisTemplate.opsForValue().set(key, DataSerializer.serializeOrException(value), ttl)
        }

    fun delete(key: String, fallbackSupplier: () -> Unit = {}) =
        execute("delete", key, fallbackSupplier) {
            redisTemplate.delete(key)
        }

    private fun <T> execute(operation: String, key: String, fallbackSupplier: () -> T?, action: () -> T?): T? =
        try {
            redisCircuitBreaker.executeSupplier { action() }
        } catch (e: Exception) {
            log.warn("[StringRedisRepository.$operation] Redis 장애. key=$key", e)
            fallbackSupplier()
        }
}