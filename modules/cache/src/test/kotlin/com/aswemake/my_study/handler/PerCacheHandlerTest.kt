package com.aswemake.my_study.handler

import com.aswemake.my_study.RedisTestContainerSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration

class PerCacheHandlerTest : RedisTestContainerSupport() {

    @Autowired
    private lateinit var cacheHandler: PerCacheHandler

    @Test
    fun put() {
        cacheHandler.put("test", Duration.ofSeconds(10), "data")

        // then
        val result = redisTemplate.opsForValue().get("test")
        assertThat(result).isNotNull
        println("result: $result")
    }

    @Test
    fun evict() {
        cacheHandler.put("test", Duration.ofSeconds(10), "data")

        cacheHandler.evict("test")

        val result = redisTemplate.opsForValue().get("test")
        assertThat(result).isNull()
    }

    @Test
    fun fetch() {
        val result1 = fetchData()
        val result2 = fetchData()
        val result3 = fetchData()

        assertThat(result1).isEqualTo("sourceData")
        assertThat(result2).isEqualTo("sourceData")
        assertThat(result3).isEqualTo("sourceData")
    }

    private fun fetchData(): String? {
        return cacheHandler.fetch(
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