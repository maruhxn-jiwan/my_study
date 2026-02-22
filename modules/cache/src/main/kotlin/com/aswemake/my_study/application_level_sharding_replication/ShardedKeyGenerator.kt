package com.aswemake.my_study.application_level_sharding_replication

import org.springframework.stereotype.Component
import java.util.random.RandomGenerator

@Component
class ShardedKeyGenerator {
    fun genShardedKeys(key: String, shardCount: Int): List<String> {
        return (0..<shardCount).map { shardIndex -> genShardedKey(key, shardIndex) }
    }

    fun findRandomShardedKey(key: String, shardCount: Int): String =
        genShardedKey(key, RandomGenerator.getDefault().nextInt(shardCount))

    private fun genShardedKey(key: String, shardIndex: Int): String = "$key:$shardIndex"
}