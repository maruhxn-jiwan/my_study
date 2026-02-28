package com.aswemake.my_study

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration

class RedisRepositoryIntegrationTest : RedisTestContainerSupport() {

    @Autowired
    private lateinit var redisRepository: RedisRepository

    @Test
    fun `set 후 get 하면 저장한 값이 반환된다`() {
        val key = "item:1"
        val value = "hello"

        redisRepository.set(key, value, Duration.ofSeconds(60))
        val result = redisRepository.get(key, String::class.java) { null }

        assertThat(result).isEqualTo(value)
    }

    @Test
    fun `delete 후 get 하면 null이 반환된다`() {
        val key = "item:1"
        redisRepository.set(key, "hello", Duration.ofSeconds(60))

        redisRepository.delete(key)
        val result = redisRepository.get(key, String::class.java) { null }

        assertThat(result).isNull()
    }

    @Test
    fun `존재하지 않는 키 get 시 null을 반환한다`() {
        val result = redisRepository.get("non-existent", String::class.java) { null }
        assertThat(result).isNull()
    }
}
