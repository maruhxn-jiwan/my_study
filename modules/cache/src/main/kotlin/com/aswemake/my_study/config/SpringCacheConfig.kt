package com.aswemake.my_study.config

import com.aswemake.my_study.MyCacheErrorHandler
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.interceptor.CacheErrorHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import tools.jackson.module.kotlin.KotlinModule
import java.time.Duration

/**
 * Spring Data Redis 를 사용한다면 Spring Boot 가 `RedisCacheManager` 를 자동으로 설정해줌
 *
 * 하지만 Redis 는 직렬화/역직렬화 때문에 별도의 캐시 설정이 필요하고 이 때 사용하는게 `RedisCacheConfiguration`
 * `RedisCacheConfiguration`을 통해 다음을 오버라이드할 수 있음
 * - computePrefixWith: Cache Key prefix 설정
 * - entryTtl: 캐시 만료 시간
 * - disableCachingNullValues: 캐싱할 때 null 값을 허용하지 않음 (#result == null 과 함께 사용해야 함)
 * - serializeKeysWith: Key 를 직렬화할 때 사용하는 규칙. 보통은 String 형태로 저장
 * - serializeValuesWith: Value 를 직렬화할 때 사용하는 규칙. Jackson2 를 많이 사용함
 *
 * 만약 캐시이름 별로 여러 세팅을 하고 싶다면 `RedisCacheManagerBuilderCustomizer` 를 선언해서 사용
 */
@Configuration
@EnableCaching
class SpringCacheConfig(
    private val redisCircuitBreaker: CircuitBreaker
) : CachingConfigurer {

    @Bean
    fun cacheManager(connectionFactory: RedisConnectionFactory): RedisCacheManager {
        val defaultCacheConfig = customCacheConfig()

        return RedisCacheManager.builder(connectionFactory)
            .withInitialCacheConfigurations(
                mapOf(
                    Pair("item", defaultCacheConfig.entryTtl(Duration.ofSeconds(10))),
                    Pair("itemList", defaultCacheConfig.entryTtl(Duration.ofSeconds(10))),
                )
            )
            .build()
    }

    private fun customCacheConfig(): RedisCacheConfiguration {
        val defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
            .computePrefixWith { "maruhxn::${it}::" }
//            .entryTtl(Duration.ofMinutes(5))
            .disableCachingNullValues()
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    GenericJacksonJsonRedisSerializer.builder()
                        .enableUnsafeDefaultTyping()
                        .customize { it.addModule(KotlinModule.Builder().build()) }
                        .build(),
                )
            )
        return defaultCacheConfig
    }

    /**
     * CachingConfigurerSupport 상속 후 errorHandler 메서드 오버라이드하여
     * 캐시 에러 핸들러 등록
     */
    override fun errorHandler(): CacheErrorHandler {
        return MyCacheErrorHandler(redisCircuitBreaker)
    }
}