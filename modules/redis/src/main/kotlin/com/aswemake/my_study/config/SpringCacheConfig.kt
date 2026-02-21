package com.aswemake.my_study.config

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import tools.jackson.module.kotlin.KotlinModule
import java.time.Duration

@Configuration
@EnableCaching
class SpringCacheConfig {

    @Bean
    fun cacheManager(connectionFactory: RedisConnectionFactory): RedisCacheManager {
        val defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
            .disableCachingNullValues()
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    GenericJacksonJsonRedisSerializer.builder()
                        .enableUnsafeDefaultTyping()
                        .customize { it.addModule(KotlinModule.Builder().build()) }
                        .build(),
                )
            )

        return RedisCacheManager.builder(connectionFactory)
            .withInitialCacheConfigurations(
                mapOf(
                    Pair("item", defaultCacheConfig.entryTtl(Duration.ofSeconds(1))),
                    Pair("itemList", defaultCacheConfig.entryTtl(Duration.ofSeconds(1))),
                )
            )
            .build()
    }
}