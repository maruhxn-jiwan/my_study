package com.aswemake.my_study.config

import io.github.resilience4j.spring6.circuitbreaker.configure.CircuitBreakerConfigurationProperties
import io.github.resilience4j.spring6.retry.configure.RetryConfigurationProperties
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration

@Configuration
class Resilience4jOrderConfig(
    val circuitBreakerConfigurationProperties: CircuitBreakerConfigurationProperties,
    val retryConfigurationProperties: RetryConfigurationProperties
) {

    companion object {
        const val PRIORITY_1 = -3
        const val PRIORITY_2 = -4
    }

    /**
     * retry 로 인한 실패 횟수 모두 CircuitBreaker에 실패로 함께 집계되어 예상보다 failure rate 에 빨리 도달 가능하므로
     * 이를 감안하여 CircuitBreaker의 failure rate 를 설정
     */
    @PostConstruct
    fun setOrder() {
        circuitBreakerConfigurationProperties.circuitBreakerAspectOrder = PRIORITY_2
        retryConfigurationProperties.retryAspectOrder = PRIORITY_1
    }
}