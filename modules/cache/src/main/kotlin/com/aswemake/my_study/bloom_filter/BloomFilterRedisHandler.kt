package com.aswemake.my_study.bloom_filter

import org.springframework.data.redis.connection.DefaultStringRedisConnection
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class BloomFilterRedisHandler(
    private val redisTemplate: StringRedisTemplate
) {
    private val CHUNK_SIZE = 8L * 1024 * 1024 * 8 // 8MB

    /**
     * 최초에 큰 메모리를 할당하려면 블로킹이 생길 수 있으며, 싱글 스레드 특성 상 다른 연산에 지연이 생길 수 있음
     * BloomFilter 활성화 전에 내부 관리 도구에서 점차 메모리를 늘려가는 전략으로 미리 필요한만큼 할당해둘 수 있음
     */
    fun init(bloomFilter: BloomFilter) {
        val key = getBloomFilterKey(bloomFilter.id)

        redisTemplate.executePipelined { action ->
            val conn = DefaultStringRedisConnection(action)
            var offset = 0L
            while (offset < bloomFilter.bitSize) {
                conn.setBit(key, offset, false)
                offset += CHUNK_SIZE
            }
            null
        }
    }

    /**
     * value를 BloomFilter에 삽입합니다
     */
    fun add(bloomFilter: BloomFilter, value: String) {
        redisTemplate.executePipelined { action ->
            val conn = DefaultStringRedisConnection(action)
            val key = getBloomFilterKey(bloomFilter.id)
            bloomFilter.hash(value).forEach {
                conn.setBit(key, it, true)
            }
            null
        }
    }

    /**
     * 해당 value의 BloomFilter 존재 여부를 확인합니다
     * 모든 비트가 1이라면, BloomFilter에 존재할 수도 있습니다
     * -> 직접 조회 필요
     *
     * 하나라도 0이라면, BloomFilter에 존재하지 않습니다
     */
    fun mightContain(bloomFilter: BloomFilter, value: String): Boolean =
        redisTemplate.executePipelined { action ->
            val conn = DefaultStringRedisConnection(action)
            val key = getBloomFilterKey(bloomFilter.id)

            bloomFilter.hash(value).forEach { conn.getBit(key, it) }
            null
        }.all { it == true }

    /**
     * BloomFilter를 제거합니다
     */
    fun delete(bloomFilter: BloomFilter) {
        redisTemplate.delete(getBloomFilterKey(bloomFilter.id))
    }

    private fun getBloomFilterKey(id: String) = "bloom_filter:${id}"
}