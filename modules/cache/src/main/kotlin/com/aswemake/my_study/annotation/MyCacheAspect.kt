package com.aswemake.my_study.annotation

import com.aswemake.my_study.CacheStrategy
import com.aswemake.my_study.MyCacheKeyGenerator
import com.aswemake.my_study.handler.MyCacheHandler
import logger
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.function.Supplier

@Aspect
@Component
class MyCacheAspect(
    private val cacheHandlers: List<MyCacheHandler>,
    private val cacheKeyGenerator: MyCacheKeyGenerator
) {

    private val log = logger<MyCacheAspect>()

    @Around("@annotation(myCacheable)")
    fun handleCacheable(joinPoint: ProceedingJoinPoint, myCacheable: MyCacheable): Any? {
        val cacheStrategy = myCacheable.strategy
        val cacheHandler = findCacheHandler(cacheStrategy)

        val key = cacheKeyGenerator.genKey(
            parameterNames = (joinPoint.signature as MethodSignature).parameterNames,
            args = joinPoint.args,
            keySpel = myCacheable.key,
            cacheStrategy = cacheStrategy,
            cacheName = myCacheable.cacheName
        )

        val ttl = Duration.ofSeconds(myCacheable.ttlSeconds)
        val dataSourceSupplier = createDataSourceSupplier(joinPoint)
        val returnType = findReturnType(joinPoint)

        try {
            log.info("[MyCacheAspect.handleCacheable] key=$key]")
            return cacheHandler.fetch(
                key = key,
                ttl = ttl,
                dataSourceSupplier = dataSourceSupplier,
                clazz = returnType
            )
        } catch (e: Exception) {
            log.error("[MyCacheAspect.handleCacheable] key=$key", e)
            return dataSourceSupplier.get()
        }
    }

    private fun findCacheHandler(cacheStrategy: CacheStrategy) =
        cacheHandlers.first { it.supports(cacheStrategy) }

    private fun createDataSourceSupplier(joinPoint: ProceedingJoinPoint): Supplier<Any?> {
        return Supplier {
            try {
                return@Supplier joinPoint.proceed()
            } catch (e: Throwable) {
                throw RuntimeException(e)
            }
        }
    }

    private fun findReturnType(joinPoint: JoinPoint): Class<Any> {
        val signature = joinPoint.signature as MethodSignature
        return signature.returnType
    }

    @AfterReturning(pointcut = "@annotation(myCachePut)", returning = "result")
    fun handleCachePut(joinPoint: JoinPoint, myCachePut: MyCachePut, result: Any?) {
        val cacheStrategy = myCachePut.strategy
        val cacheHandler = findCacheHandler(cacheStrategy)
        val key = cacheKeyGenerator.genKey(
            parameterNames = (joinPoint.signature as MethodSignature).parameterNames,
            args = joinPoint.args,
            keySpel = myCachePut.key,
            cacheStrategy = cacheStrategy,
            cacheName = myCachePut.cacheName
        )
        log.info("[MyCacheAspect.handleCachePut] key={}", key)
        cacheHandler.put(key, Duration.ofSeconds(myCachePut.ttlSeconds), result)
    }

    @AfterReturning(pointcut = "@annotation(myCacheEvict)")
    fun handleCacheEvict(joinPoint: JoinPoint, myCacheEvict: MyCacheEvict) {
        val cacheStrategy = myCacheEvict.strategy
        val cacheHandler = findCacheHandler(cacheStrategy)
        val key = cacheKeyGenerator.genKey(
            parameterNames = (joinPoint.signature as MethodSignature).parameterNames,
            args = joinPoint.args,
            keySpel = myCacheEvict.key,
            cacheStrategy = cacheStrategy,
            cacheName = myCacheEvict.cacheName
        )
        log.info("[MyCacheAspect.handleCacheEvict] key={}", key)
        cacheHandler.evict(key)
    }
}