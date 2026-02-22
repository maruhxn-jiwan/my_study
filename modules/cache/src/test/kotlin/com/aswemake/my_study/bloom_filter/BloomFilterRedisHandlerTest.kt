package com.aswemake.my_study.bloom_filter

import com.aswemake.my_study.RedisTestContainerSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration

class BloomFilterRedisHandlerTest : RedisTestContainerSupport() {

    @Autowired
    private lateinit var bloomFilterRedisHandler: BloomFilterRedisHandler

    @Test
    fun add() {
        // given
        val bloomFilter = BloomFilter.create(
            id = "test",
            dataCount = 1000,
            falsePositiveRate = 0.01
        )

        // when
        bloomFilterRedisHandler.add(bloomFilter, "value")

        // then
        val hashedIndexes = bloomFilter.hash("value")
        for (offset in 0..<bloomFilter.bitSize) {
            val result = redisTemplate.opsForValue().getBit("bloom_filter:${bloomFilter.id}", offset)
            assertThat(result).isEqualTo(hashedIndexes.contains(offset))
        }
    }

    @Test
    fun delete() {
        // given
        val bloomFilter = BloomFilter.create(
            id = "test",
            dataCount = 1000,
            falsePositiveRate = 0.01
        )
        bloomFilterRedisHandler.add(bloomFilter, "value")

        // when
        bloomFilterRedisHandler.delete(bloomFilter)

        // then
        for (offset in 0..<bloomFilter.bitSize) {
            val result = redisTemplate.opsForValue().getBit("bloom_filter:${bloomFilter.id}", offset)
            assertThat(result).isFalse
        }
    }

    @Test
    fun mightContain() {
        // given
        val bloomFilter = BloomFilter.create(
            id = "test",
            dataCount = 1000,
            falsePositiveRate = 0.01
        )

        val values = (0..<1000).map { "value$it" }
        values.forEach { bloomFilterRedisHandler.add(bloomFilter, it) }

        // when, then
        values.forEach {
            val result = bloomFilterRedisHandler.mightContain(bloomFilter, it)
            assertThat(result).isTrue
        }

        for (i in 0..<10000) {
            val value = "notAddedValue${i}"
            val result = bloomFilterRedisHandler.mightContain(bloomFilter, value)
            if (result) {
                println("value = $value")
            }
        }
    }

    @Test
    fun `많은 데이터 삽입 테스트 (init X)`() {
        val bloomFilter = BloomFilter.create(
            id = "test",
            dataCount = 400_000_000,
            falsePositiveRate = 0.01
        )
        val hashedIndexes = bloomFilter.hash("value")
        println("bloomFilter.bitSize=${bloomFilter.bitSize}")
        println("hashedIndexes=${hashedIndexes}")

        val start = System.nanoTime()
        bloomFilterRedisHandler.add(bloomFilter, "value")
        val timeMillis = Duration.ofNanos(System.nanoTime() - start).toMillis()
        println("timeMillis=${timeMillis}") // 약 300ms 소요
    }

    @Test
    fun `많은 데이터 삽입 테스트 (init O)`() {
        val bloomFilter = BloomFilter.create(
            id = "test",
            dataCount = 400_000_000,
            falsePositiveRate = 0.01
        )
        val hashedIndexes = bloomFilter.hash("value")
        println("bloomFilter.bitSize=${bloomFilter.bitSize}")
        println("hashedIndexes=${hashedIndexes}")

        bloomFilterRedisHandler.init(bloomFilter)

        val start = System.nanoTime()
        bloomFilterRedisHandler.add(bloomFilter, "value")
        val timeMillis = Duration.ofNanos(System.nanoTime() - start).toMillis()
        println("timeMillis=${timeMillis}") // 1ms 소요
    }

    @Test
    fun `BloomFilter에 수용 가능한 데이터 수보다 많은 데이터가 삽입되었을 경우 mightContain 동작 확인`() {
        val bloomFilter = BloomFilter.create(
            id = "test",
            dataCount = 1000,
            falsePositiveRate = 0.01
        )
        val values = (0..<2000).map { "value$it" }
        values.forEach { bloomFilterRedisHandler.add(bloomFilter, it) }

        // when, then
        values.forEach {
            val result = bloomFilterRedisHandler.mightContain(bloomFilter, it)
            assertThat(result).isTrue
        }

        // Cache Penetration 빈도 증가 (1500 / 10000) -> 15%
        for (i in 0..<10000) {
            val value = "notAddedValue${i}"
            val result = bloomFilterRedisHandler.mightContain(bloomFilter, value)
            if (result) {
                // false positive
                println("value = $value")
            }
        }
    }
}