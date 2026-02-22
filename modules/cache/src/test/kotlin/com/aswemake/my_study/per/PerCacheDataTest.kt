package com.aswemake.my_study.per

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.concurrent.TimeUnit

class PerCacheDataTest {

    @Test
    fun parseData() {
        val cacheData = PerCacheData.of(1234L, 1000L, Duration.ofSeconds(10))
        println("cacheData: $cacheData")

        assertThat(cacheData.data).isEqualTo("1234")
        assertThat(cacheData.parseData(Long::class.java)).isEqualTo(1234L)
    }

    @Test
    fun shouldRecompute() {
        val cacheData = PerCacheData.of(1234L, 1000L, Duration.ofSeconds(3))
        for (i in 0..<30) {
            val result = cacheData.shouldRecompute(1.0)
            println("result: $result")
            TimeUnit.MILLISECONDS.sleep(100)
        }
    }
}