package com.aswemake.my_study

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import logger
import org.springframework.cache.Cache
import org.springframework.cache.interceptor.CacheErrorHandler
import java.util.concurrent.TimeUnit

class MyCacheErrorHandler(
    private val redisCircuitBreaker: CircuitBreaker
) : CacheErrorHandler {
    private val log = logger<MyCacheErrorHandler>()

    /**
     * Redis 에러 발생 시, 예외 캐치 후 서킷 브레이커에 수동으로 실패 기록
     */
    override fun handleCacheGetError(
        exception: RuntimeException,
        cache: Cache,
        key: Any
    ) {
        log.warn("[MyCacheErrorHandler.handleCacheGetError] 캐시 GET 에러. key: $key", exception)
        redisCircuitBreaker.onError(0, TimeUnit.SECONDS, exception)
    }

    override fun handleCachePutError(
        exception: RuntimeException,
        cache: Cache,
        key: Any,
        value: Any?
    ) {
        log.warn("[MyCacheErrorHandler.handleCachePutError] 캐시 Put 에러. key: $key", exception)
    }

    override fun handleCacheEvictError(
        exception: RuntimeException,
        cache: Cache,
        key: Any
    ) {
        log.warn("[MyCacheErrorHandler.handleCacheEvictError] 캐시 Evict 에러. key: $key", exception)
    }

    override fun handleCacheClearError(exception: RuntimeException, cache: Cache) {
        log.warn("[MyCacheErrorHandler.handleCacheClearError] 캐시 Clear 에러.", exception)
    }
}