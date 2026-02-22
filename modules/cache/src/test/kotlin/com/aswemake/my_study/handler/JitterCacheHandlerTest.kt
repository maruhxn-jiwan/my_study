package com.aswemake.my_study.handler

import com.aswemake.my_study.RedisTestContainerSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.util.concurrent.TimeUnit

class JitterCacheHandlerTest : RedisTestContainerSupport() {

    @Autowired
    private lateinit var jitterCacheHandler: JitterCacheHandler

    @Test
    fun put() {
        jitterCacheHandler.put(
            key = "test",
            ttl = Duration.ofSeconds(10),
            value = String::class.java
        )

        // then
        val ttlSeconds = redisTemplate.getExpire("test", TimeUnit.SECONDS)
        println("ttlSeconds: $ttlSeconds")
        assertThat(ttlSeconds).isGreaterThanOrEqualTo(7)
        assertThat(ttlSeconds).isLessThanOrEqualTo(13)
    }

    @Test
    fun `ttl이 jitter 범위 이하일 경우, 에러를 반환한다`() {
        assertThrows<IllegalArgumentException> {
            jitterCacheHandler.put(
                key = "test",
                ttl = Duration.ofSeconds(3),
                value = String::class.java
            )
        }
    }

    @Test
    fun evict() {
        // given
        jitterCacheHandler.put(
            key = "test",
            ttl = Duration.ofSeconds(10),
            value = String::class.java
        )

        // when
        jitterCacheHandler.evict(key = "test")

        // then
        val result = redisTemplate.opsForValue().get("test")
        assertThat(result).isNull()
    }

    @Test
    fun fetch() {
        val result1: String = fetchData()
        val result2: String = fetchData()
        val result3: String = fetchData()

        assertThat(result1).isEqualTo("sourceData")
        assertThat(result2).isEqualTo("sourceData")
        assertThat(result3).isEqualTo("sourceData")
    }

    private fun fetchData(): String {
        return jitterCacheHandler.fetch(
            key = "test",
            ttl = Duration.ofSeconds(10),
            dataSourceSupplier = {
                println("fetch source data")
                return@fetch "sourceData"
            },
            clazz = String::class.java
        )
    }
}