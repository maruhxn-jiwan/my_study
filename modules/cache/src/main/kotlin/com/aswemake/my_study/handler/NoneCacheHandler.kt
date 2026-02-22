package com.aswemake.my_study.handler

import com.aswemake.my_study.CacheStrategy
import logger
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.function.Supplier

/**
 * 어떠한 캐시 전략도 사용하지 않을 경우 사용하는 핸들러
 */
@Component
class NoneCacheHandler : MyCacheHandler {

    private val log = logger<NoneCacheHandler>()

    override fun <T> fetch(
        key: String,
        ttl: Duration,
        dataSourceSupplier: Supplier<T?>,
        clazz: Class<T>
    ): T? {
        log.info("[NoneCacheHandler.fetch] key=$key")
        return dataSourceSupplier.get()
    }

    override fun put(key: String, ttl: Duration, value: Any?) {
        log.info("[NoneCacheHandler.put] key=$key")
    }

    override fun evict(key: String) {
        log.info("[NoneCacheHandler.evict] key=$key")
    }

    override fun supports(cacheStrategy: CacheStrategy) = CacheStrategy.NONE == cacheStrategy
}