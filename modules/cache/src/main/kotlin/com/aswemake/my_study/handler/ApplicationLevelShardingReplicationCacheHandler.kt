package com.aswemake.my_study.handler

import com.aswemake.my_study.CacheStrategy
import com.aswemake.my_study.DataSerializer
import com.aswemake.my_study.application_level_sharding_replication.ShardedKeyGenerator
import logger
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.function.Supplier

@Component
class ApplicationLevelShardingReplicationCacheHandler(
    private val redisTemplate: StringRedisTemplate,
    private val shardedKeyGenerator: ShardedKeyGenerator,
) : MyCacheHandler {
    private val log = logger<ApplicationLevelShardingReplicationCacheHandler>()

    companion object {
        // 커질수록 쓰기 비용은 높고 공간 효율 안좋을 수 있지만, 읽기는 더욱 잘 분산될 수 있음
        private val SHARD_REPLICATION_COUNT = 3
    }

    override fun <T> fetch(
        key: String,
        ttl: Duration,
        dataSourceSupplier: Supplier<T?>,
        clazz: Class<T>
    ): T? {
        val shardedKey = shardedKeyGenerator.findRandomShardedKey(key, SHARD_REPLICATION_COUNT)
        val cached = redisTemplate.opsForValue().get(shardedKey)
            ?: return refresh(key, ttl, dataSourceSupplier)

        val data = DataSerializer.deserializeOrNull(cached, clazz)
            ?: return refresh(key, ttl, dataSourceSupplier)

        return data
    }

    private fun <T> refresh(key: String, ttl: Duration, dataSourceSupplier: Supplier<T?>): T? {
        val sourceResult = dataSourceSupplier.get()
        put(key, ttl, sourceResult)
        return sourceResult
    }

    override fun put(key: String, ttl: Duration, value: Any?) {
        val serializedValue = DataSerializer.serializeOrException(value)
        val shardedKeys = shardedKeyGenerator.genShardedKeys(key, SHARD_REPLICATION_COUNT)
        shardedKeys.forEach { shardedKey ->
            log.info("[ApplicationLevelShardingReplicationCacheHandler.put] shardedKey: $shardedKey")
            redisTemplate.opsForValue().set(shardedKey, serializedValue, ttl)
        }
    }

    override fun evict(key: String) {
        val shardedKeys = shardedKeyGenerator.genShardedKeys(key, SHARD_REPLICATION_COUNT)
        shardedKeys.forEach { shardedKey ->
            log.info("[ApplicationLevelShardingReplicationCacheHandler.evict] shardedKey: $shardedKey")
            redisTemplate.delete(shardedKey)
        }
    }

    override fun supports(cacheStrategy: CacheStrategy) =
        CacheStrategy.APPLICATION_LEVEL_SHARDING_REPLICATION == cacheStrategy
}