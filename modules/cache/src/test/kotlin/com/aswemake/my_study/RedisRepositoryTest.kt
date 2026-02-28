package com.aswemake.my_study

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.time.Duration

class RedisRepositoryTest {

    private lateinit var redisTemplate: StringRedisTemplate
    private lateinit var valueOps: ValueOperations<String, String>
    private lateinit var circuitBreaker: CircuitBreaker
    private lateinit var sut: RedisRepository

    @BeforeEach
    fun setUp() {
        redisTemplate = mockk()
        valueOps = mockk()
        every { redisTemplate.opsForValue() } returns valueOps

        val config = CircuitBreakerConfig.custom()
            .minimumNumberOfCalls(2)
            .failureRateThreshold(50f)
            .build()
        circuitBreaker = CircuitBreakerRegistry.of(config).circuitBreaker("test")

        sut = RedisRepository(redisTemplate, circuitBreaker)
    }

    @Test
    fun `get - Redis에 값이 있으면 역직렬화된 값을 반환한다`() {
        val key = "item:1"
        val json = "\"hello\""
        every { valueOps.get(key) } returns json

        val result = sut.get(key, String::class.java) { "fallback" }

        assertThat(result).isEqualTo("hello")
    }

    @Test
    fun `get - Redis에 값이 없으면 null을 반환한다`() {
        val key = "item:1"
        every { valueOps.get(key) } returns null
        var fallbackCalled = false

        val result = sut.get(key, String::class.java) { fallbackCalled = true; "fallback" }

        assertThat(result).isNull()
        assertThat(fallbackCalled).isFalse()
    }

    @Test
    fun `get - Redis 예외 시 fallbackSupplier를 호출한다`() {
        val key = "item:1"
        every { valueOps.get(key) } throws RuntimeException("Redis 연결 실패")

        val result = sut.get(key, String::class.java) { "fallback" }

        assertThat(result).isEqualTo("fallback")
    }

    @Test
    fun `get - 서킷 OPEN 시 fallbackSupplier를 호출한다`() {
        circuitBreaker.transitionToOpenState()

        val result = sut.get("item:1", String::class.java) { "fallback" }

        assertThat(result).isEqualTo("fallback")
    }

    @Test
    fun `set - 정상 동작 시 Redis에 저장한다`() {
        val key = "item:1"
        val value = "hello"
        val ttl = Duration.ofSeconds(60)
        every { valueOps.set(key, any(), ttl) } returns Unit

        sut.set(key, value, ttl)

        verify { valueOps.set(key, any(), ttl) }
    }

    @Test
    fun `set - Redis 예외 시 fallbackSupplier를 호출한다`() {
        every { valueOps.set(any(), any(), any<Duration>()) } throws RuntimeException("Redis 연결 실패")
        var fallbackCalled = false

        sut.set("item:1", "hello", Duration.ofSeconds(60)) { fallbackCalled = true }

        assertThat(fallbackCalled).isTrue()
    }

    @Test
    fun `delete - 정상 동작 시 Redis 키를 삭제한다`() {
        val key = "item:1"
        every { redisTemplate.delete(key) } returns true

        sut.delete(key)

        verify { redisTemplate.delete(key) }
    }

    @Test
    fun `delete - Redis 예외 시 fallbackSupplier를 호출한다`() {
        every { redisTemplate.delete(any<String>()) } throws RuntimeException("Redis 연결 실패")
        var fallbackCalled = false

        sut.delete("item:1") { fallbackCalled = true }

        assertThat(fallbackCalled).isTrue()
    }

    @Test
    fun `set - 서킷 OPEN 시 fallbackSupplier를 호출한다`() {
        circuitBreaker.transitionToOpenState()
        var fallbackCalled = false

        sut.set("item:1", "hello", Duration.ofSeconds(60)) { fallbackCalled = true }

        assertThat(fallbackCalled).isTrue()
    }

    @Test
    fun `delete - 서킷 OPEN 시 fallbackSupplier를 호출한다`() {
        circuitBreaker.transitionToOpenState()
        var fallbackCalled = false

        sut.delete("item:1") { fallbackCalled = true }

        assertThat(fallbackCalled).isTrue()
    }
}
