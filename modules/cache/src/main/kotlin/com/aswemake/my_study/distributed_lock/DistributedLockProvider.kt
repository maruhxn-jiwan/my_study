package com.aswemake.my_study.distributed_lock

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class DistributedLockProvider(
    private val redisTemplate: StringRedisTemplate,
) {

    fun lock(id: String, ttl: Duration): Boolean {
        val result = redisTemplate.opsForValue().setIfAbsent(genKey(id), "", ttl)
        return result != null && result
    }

    fun unlock(id: String) {
        redisTemplate.delete(genKey(id))
    }

    private fun genKey(id: String) = "distributed_lock:$id"
}