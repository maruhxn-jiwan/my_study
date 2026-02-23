package com.aswemake.my_study

import logger
import org.springframework.cache.Cache
import org.springframework.cache.interceptor.CacheErrorHandler

class MyCacheErrorHandler : CacheErrorHandler {
    private val log = logger<MyCacheErrorHandler>()

    override fun handleCacheGetError(
        exception: RuntimeException,
        cache: Cache,
        key: Any
    ) {
        log.warn("[MyCacheErrorHandler.handleCacheGetError] 캐시 GET 에러. key: $key", exception)
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