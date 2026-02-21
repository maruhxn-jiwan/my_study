package com.aswemake.my_study.api

import com.aswemake.my_study.CacheStrategy
import org.junit.jupiter.api.Test

class NullObjectPatternStrategyApiTest {

    @Test
    fun read() {
        (0..2).forEach {
            val item = ItemApiTestUtils.read(CACHE_STRATEGY, 99999L)
            println(item)
        }
    }

    companion object {
        val CACHE_STRATEGY: CacheStrategy = CacheStrategy.NULL_OBJECT_PATTERN
    }
}