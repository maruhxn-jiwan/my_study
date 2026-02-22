package com.aswemake.my_study.handler

import com.aswemake.my_study.CacheStrategy
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import java.time.Duration
import java.util.function.Supplier
import java.util.random.RandomGenerator

@Component
class JitterCacheHandler(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper,
) : MyCacheHandler {

    companion object {
        private val JITTER_RANGE_SECONDS = 3
    }


    override fun <T> fetch(
        key: String,
        ttl: Duration,
        dataSourceSupplier: Supplier<T?>,
        clazz: Class<T>
    ): T? {
        val cached = redisTemplate.opsForValue().get(key)
        // 캐시에 없으면 DataSource로부터 가져오고, 캐시에 PUT
            ?: return refresh(key, ttl, dataSourceSupplier)


        val data: T = objectMapper.readValue(cached, clazz)
        // deserialize 실패 시, 다시 refresh
            ?: return refresh(key, ttl, dataSourceSupplier)

        return data
    }

    private fun <T> refresh(key: String?, ttl: Duration, dataSourceSupplier: Supplier<T?>): T? {
        val sourceResult = dataSourceSupplier.get()
        put(key!!, ttl, sourceResult)
        return sourceResult
    }

    override fun put(key: String, ttl: Duration, value: Any?) {
        val data = objectMapper.writeValueAsString(value)
        redisTemplate.opsForValue().set(key, data, applyJitter(ttl))
    }

    private fun applyJitter(ttl: Duration): Duration {
        if (ttl.seconds <= JITTER_RANGE_SECONDS) {
            throw IllegalArgumentException("Jitter ttl must be greater than ${JITTER_RANGE_SECONDS}")
        }

        // -3 <= jitter <= +3
        val jitter = RandomGenerator.getDefault().nextInt(-JITTER_RANGE_SECONDS, JITTER_RANGE_SECONDS + 1)
        return ttl.plusSeconds(jitter.toLong())
    }

    override fun evict(key: String) {
        redisTemplate.delete(key)
    }

    override fun supports(cacheStrategy: CacheStrategy): Boolean =
        CacheStrategy.JITTER == cacheStrategy
}