package com.aswemake.my_study.api

import com.aswemake.my_study.CacheStrategy
import com.aswemake.my_study.domain.command.ItemCreateCommand
import org.junit.jupiter.api.Test

class BloomFilterStrategyApiTest {

    @Test
    fun test() {
        (0..<1000).forEach {
            ItemApiTestUtils.create(CACHE_STRATEGY, ItemCreateCommand("data${it}"))
        }

        (10000..<20000).forEach {
            ItemApiTestUtils.read(CACHE_STRATEGY, it.toLong())
        }
    }

    companion object {
        val CACHE_STRATEGY: CacheStrategy = CacheStrategy.BLOOM_FILTER
    }
}