package com.aswemake.my_study.api

import com.aswemake.my_study.CacheStrategy
import com.aswemake.my_study.domain.command.ItemCreateCommand
import com.aswemake.my_study.domain.command.ItemUpdateCommand
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Disabled
class ItemPerStrategyApiTest {

    @Test
    fun test() {
        val item = ItemApiTestUtils.create(CACHE_STRATEGY, ItemCreateCommand("data"))

        val es = Executors.newFixedThreadPool(3)
        val start = System.nanoTime()
        while (System.nanoTime() - start < TimeUnit.SECONDS.toNanos(20)) {
            for (i in 0..2) {
                es.execute { ItemApiTestUtils.read(CACHE_STRATEGY, item.itemId) }
            }

            TimeUnit.MILLISECONDS.sleep(10)
        }

        ItemApiTestUtils.update(CACHE_STRATEGY, item.itemId, ItemUpdateCommand("updated"))
        val updated = ItemApiTestUtils.read(CACHE_STRATEGY, item.itemId)
        println("updated: $updated")

        ItemApiTestUtils.delete(CACHE_STRATEGY, item.itemId)
        val deleted = ItemApiTestUtils.read(CACHE_STRATEGY, item.itemId)
        println("deleted: $deleted")
    }

    companion object {
        val CACHE_STRATEGY = CacheStrategy.PROBABILISTIC_EARLY_RECOMPUTATION
    }


}