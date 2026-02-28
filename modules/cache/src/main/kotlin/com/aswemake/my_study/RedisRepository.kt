package com.aswemake.my_study

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import logger
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisRepository(
    private val redisTemplate: StringRedisTemplate,
    private val redisCircuitBreaker: CircuitBreaker,
) {
    private val log = logger<RedisRepository>()

    fun <T> get(key: String, clazz: Class<T>, fallbackSupplier: () -> T?): T? =
        try {
            executeWithCircuitBreaker { redisTemplate.opsForValue().get(key)?.let { DataSerializer.deserializeOrNull(it, clazz) } }
        } catch (e: Exception) {
            log.warn("[RedisRepository.get] Redis 장애. key=$key", e)
            fallbackSupplier()
        }

    fun set(key: String, value: Any, ttl: Duration, fallbackSupplier: () -> Unit = {}) {
        try {
            executeWithCircuitBreaker { redisTemplate.opsForValue().set(key, DataSerializer.serializeOrException(value), ttl) }
        } catch (e: Exception) {
            log.warn("[RedisRepository.set] Redis 장애. key=$key", e)
            fallbackSupplier()
        }
    }

    fun delete(key: String, fallbackSupplier: () -> Unit = {}) {
        try {
            executeWithCircuitBreaker { redisTemplate.delete(key) }
        } catch (e: Exception) {
            log.warn("[RedisRepository.delete] Redis 장애. key=$key", e)
            fallbackSupplier()
        }
    }

    private fun <T> executeWithCircuitBreaker(action: () -> T): T =
        redisCircuitBreaker.executeSupplier { action() }
}
