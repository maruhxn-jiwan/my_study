package com.aswemake.my_study

import org.junit.jupiter.api.Test

class RedisTest : RedisTestContainerSupport() {
    @Test
    fun test1() {
        redisTemplate.opsForValue().set("myKey", "myValue")
        val result = redisTemplate.opsForValue().get("myKey")
        println(result)
    }
}
