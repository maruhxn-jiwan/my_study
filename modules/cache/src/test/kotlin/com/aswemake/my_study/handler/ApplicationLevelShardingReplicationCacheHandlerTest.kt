package com.aswemake.my_study.handler

import com.aswemake.my_study.DataSerializer
import com.aswemake.my_study.RedisTestContainerSupport
import com.aswemake.my_study.application_level_sharding_replication.ShardedKeyGenerator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration

class ApplicationLevelShardingReplicationCacheHandlerTest : RedisTestContainerSupport() {

    @Autowired
    private lateinit var cacheHandler: ApplicationLevelShardingReplicationCacheHandler

    @Autowired
    private lateinit var shardedKeyGenerator: ShardedKeyGenerator

    @Test
    fun put() {
        cacheHandler.put("test", Duration.ofSeconds(3), "data")
        val shardedKeys = shardedKeyGenerator.genShardedKeys("test", 3)
        shardedKeys.forEach {
            val result = redisTemplate.opsForValue().get(it)
            assertThat(DataSerializer.deserializeOrNull(result!!, String::class.java)).isEqualTo("data")
        }
    }

    @Test
    fun evict() {
        // given
        cacheHandler.put("test", Duration.ofSeconds(3), "data")

        // when
        cacheHandler.evict("test")

        // then
        val shardedKeys = shardedKeyGenerator.genShardedKeys("test", 3)
        shardedKeys.forEach {
            val result = redisTemplate.opsForValue().get(it)
            assertThat(result).isNull()
        }
    }

    @Test
    fun fetch() {
        val result1 = fetchData()
        val result2 = fetchData()
        val result3 = fetchData()

        assertThat(result1).isEqualTo("sourceData")
        assertThat(result2).isEqualTo("sourceData")
        assertThat(result3).isEqualTo("sourceData")
    }

    private fun fetchData(): String? {
        return cacheHandler.fetch(
            key = "test",
            ttl = Duration.ofSeconds(10),
            dataSourceSupplier = {
                println("fetch source data")
                return@fetch "sourceData"
            },
            clazz = String::class.java
        )
    }
}