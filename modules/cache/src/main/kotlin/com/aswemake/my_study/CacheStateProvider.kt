package com.aswemake.my_study

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import org.springframework.stereotype.Component

@Component
class CacheStateProvider(
    private val redisCircuitBreaker: CircuitBreaker
) {
    /**
     * 서킷 OPEN 여부를 통해 캐시 사용 가능 여부 확인
     */
    fun isCacheEnabled() = redisCircuitBreaker.state != CircuitBreaker.State.OPEN
}