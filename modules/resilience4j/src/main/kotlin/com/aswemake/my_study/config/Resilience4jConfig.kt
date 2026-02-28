package com.aswemake.my_study.config

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class Resilience4jConfig(
    val circuitBreakerProperty: CircuitBreakerProperty,
) {

    companion object {
        const val CIRCUIT_REDIS: String = "CB_REDIS"
    }

    @Bean
    fun redisCircuitBreaker(
        registry: CircuitBreakerRegistry,
        redisCircuitBreakerConfig: CircuitBreakerConfig,
    ): CircuitBreaker {
        return registry.circuitBreaker(CIRCUIT_REDIS, redisCircuitBreakerConfig)
    }

    @Bean
    fun redisCircuitBreakerConfig(): CircuitBreakerConfig {
        return CircuitBreakerConfig.custom()
            .failureRateThreshold(circuitBreakerProperty.failureRateThreshold)
            .slowCallDurationThreshold(Duration.ofMillis(circuitBreakerProperty.slowCallDurationThreshold))
            .slowCallRateThreshold(circuitBreakerProperty.slowCallRateThreshold)
            .waitDurationInOpenState(Duration.ofMillis(circuitBreakerProperty.waitDurationInOpenState))
            .minimumNumberOfCalls(circuitBreakerProperty.minimumNumberOfCalls)
            .slidingWindowSize(circuitBreakerProperty.slidingWindowSize)
            .permittedNumberOfCallsInHalfOpenState(circuitBreakerProperty.permittedNumberOfCallsInHalfOpenState)
            .build()
    }
}