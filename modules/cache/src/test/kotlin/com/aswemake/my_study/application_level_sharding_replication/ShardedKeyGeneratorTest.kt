package com.aswemake.my_study.application_level_sharding_replication

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ShardedKeyGeneratorTest {
    private val shardedKeyGenerator = ShardedKeyGenerator()

    @Test
    fun genShardedKeys() {
        // given
        val key = "testKey"
        val shardCount = 3

        // when
        val shardedKeys = shardedKeyGenerator.genShardedKeys(key, shardCount)

        // then
        assertThat(shardedKeys).hasSize(shardCount)
        (0..<shardCount).forEach { assertThat(shardedKeys[it]).isEqualTo("$key:$it") }
    }

    @Test
    fun findRandomShardedKey() {
        // given
        val key = "testKey"
        val shardCount = 3

        // when, then
        val shardedKeys = shardedKeyGenerator.genShardedKeys(key, shardCount)
        (0..<10).forEach {
            val randomKey = shardedKeyGenerator.findRandomShardedKey(key, shardCount)
            assertThat(randomKey).isIn(shardedKeys)
            println("randomKey = $randomKey")
        }
    }
}