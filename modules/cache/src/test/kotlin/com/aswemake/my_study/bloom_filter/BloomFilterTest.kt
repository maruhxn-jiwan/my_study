package com.aswemake.my_study.bloom_filter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BloomFilterTest {

    @Test
    fun create() {
        val bloomFilter1 = BloomFilter.create("testId1", 1000, 0.01)
        assertThat(bloomFilter1.id).isEqualTo("testId1")
        assertThat(bloomFilter1.dataCount).isEqualTo(1000)
        assertThat(bloomFilter1.falsePositiveRate).isEqualTo(0.01)
        assertThat(bloomFilter1.bitSize).isEqualTo(9586)
        assertThat(bloomFilter1.hashFunctionCount).isEqualTo(7)
        println("bloomFilter1 = $bloomFilter1")

        val bloomFilter2 = BloomFilter.create("testId2", 100000000, 0.01)
        assertThat(bloomFilter2.id).isEqualTo("testId2")
        assertThat(bloomFilter2.dataCount).isEqualTo(100000000)
        assertThat(bloomFilter2.falsePositiveRate).isEqualTo(0.01)
        assertThat(bloomFilter2.bitSize).isEqualTo(958505838)
        assertThat(bloomFilter2.hashFunctionCount).isEqualTo(7)
        println("bloomFilter2 = $bloomFilter2")
    }
}