package com.aswemake.my_study.config

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class Resilience4jConfig {

    @Bean
    fun redisCircuitBreakerConfig(): CircuitBreakerConfig {
        return CircuitBreakerConfig.custom()
            .failureRateThreshold(50F) // 실패 비율이 50% 잉상이면 서킷 오픈
            .waitDurationInOpenState(Duration.ofSeconds(10)) // 서킷이 오픈 상태 유지하는 시간
            .slidingWindowSize(20)
            .minimumNumberOfCalls(2) // 서킷이 오픈되기 전, 최소 요청 횟수
            .build()
    }

    @Bean
    fun redisCircuitBreaker(
        registry: CircuitBreakerRegistry,
        redisCircuitBreakerConfig: CircuitBreakerConfig,
    ): CircuitBreaker {
        return registry.circuitBreaker("redis", redisCircuitBreakerConfig)
    }
}