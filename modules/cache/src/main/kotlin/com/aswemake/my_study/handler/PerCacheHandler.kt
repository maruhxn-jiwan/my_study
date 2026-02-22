package com.aswemake.my_study.handler

import com.aswemake.my_study.CacheStrategy
import com.aswemake.my_study.per.PerCacheData
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import java.time.Duration
import java.time.Instant
import java.util.function.Supplier

@Component
class PerCacheHandler(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper,
) : MyCacheHandler {
    override fun <T> fetch(
        key: String,
        ttl: Duration,
        dataSourceSupplier: Supplier<T>,
        clazz: Class<T>
    ): T {
        println("fetching $key")
        val cached = redisTemplate.opsForValue().get(key)
            ?: return refresh(key, ttl, dataSourceSupplier)

        val cachedData: PerCacheData = objectMapper.readValue(cached, PerCacheData::class.java)
            ?: return refresh(key, ttl, dataSourceSupplier)

        if (cachedData.shouldRecompute(1.0)) {
            return refresh(key, ttl, dataSourceSupplier)
        }

        val data = cachedData.parseData(clazz)
            ?: return refresh(key, ttl, dataSourceSupplier)

        return data
    }

    private fun <T> refresh(key: String?, ttl: Duration?, dataSourceSupplier: Supplier<T>): T {
        println("refresh")
        val startMillis = Instant.now().toEpochMilli()
        val sourceResult = dataSourceSupplier.get()
        val computationTimeMillis = Instant.now().toEpochMilli() - startMillis
        put(key!!, ttl!!, sourceResult, computationTimeMillis)
        return sourceResult
    }

    private fun put(key: String, ttl: Duration, data: Any?, computationTimeMillis: Long) {
        val cacheData = PerCacheData.of(data = data, computationTimeMillis = computationTimeMillis, ttl = ttl)
        redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(cacheData), ttl)

    }

    override fun put(key: String, ttl: Duration, value: Any?) {
        put(key, ttl, value, 100)
    }

    override fun evict(key: String) {
        redisTemplate.delete(key)
    }

    override fun supports(cacheStrategy: CacheStrategy) =
        CacheStrategy.PROBABILISTIC_EARLY_RECOMPUTATION == cacheStrategy
}