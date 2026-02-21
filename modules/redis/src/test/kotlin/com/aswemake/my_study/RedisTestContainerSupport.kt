package com.aswemake.my_study

import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.StringRedisTemplate

@SpringBootTest(classes = [RedisTestContainerSupport.Config::class])
@Import(RedisTestcontainersConfiguration::class)
abstract class RedisTestContainerSupport {

    @SpringBootApplication
    class Config

    @Autowired
    protected lateinit var redisTemplate: StringRedisTemplate

    @BeforeEach
    fun cleanData() {
        redisTemplate.connectionFactory?.connection?.serverCommands()?.flushDb()
    }

}
