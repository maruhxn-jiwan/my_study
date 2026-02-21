package com.aswemake.my_study.api

import com.aswemake.my_study.CacheStrategy
import com.aswemake.my_study.domain.command.ItemCreateCommand
import com.aswemake.my_study.domain.command.ItemUpdateCommand
import org.junit.jupiter.api.Test

class NoneStrategyApiTest {
    @Test
    fun createAndReadAndUpdateAndDelete() {
        val created = ItemApiTestUtils.create(CACHE_STRATEGY, ItemCreateCommand("data"))
        println("created = " + created)

        val read1 = ItemApiTestUtils.read(CACHE_STRATEGY, created.itemId)!!
        println("read1 = " + read1)

        val updated = ItemApiTestUtils.update(
            CACHE_STRATEGY,
            read1.itemId,
            ItemUpdateCommand("updatedData")
        )
        println("updated = " + updated)

        val read2 = ItemApiTestUtils.read(CACHE_STRATEGY, read1.itemId)
        println("read2 = " + read2)

        ItemApiTestUtils.delete(CACHE_STRATEGY, read1.itemId)

        val read3 = ItemApiTestUtils.read(CACHE_STRATEGY, read1.itemId)
        println("read3 = " + read3)
    }

    @Test
    fun readAll() {
        for (i in 0..2) {
            ItemApiTestUtils.create(CACHE_STRATEGY, ItemCreateCommand("data" + i))
        }

        val itemPage1 = ItemApiTestUtils.readAll(CACHE_STRATEGY, 1L, 2L)
        println("itemPage1 = " + itemPage1)

        val itemPage2 = ItemApiTestUtils.readAll(CACHE_STRATEGY, 2L, 2L)
        println("itemPage2 = " + itemPage2)
    }

    companion object {
        val CACHE_STRATEGY: CacheStrategy = CacheStrategy.NONE
    }
}
