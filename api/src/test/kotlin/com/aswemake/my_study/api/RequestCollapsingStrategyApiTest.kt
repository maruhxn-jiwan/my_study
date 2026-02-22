package com.aswemake.my_study.api

import com.aswemake.my_study.CacheStrategy
import com.aswemake.my_study.domain.command.ItemCreateCommand
import com.aswemake.my_study.domain.command.ItemUpdateCommand
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class RequestCollapsingStrategyApiTest {

    @Test
    fun test() {
        val item = ItemApiTestUtils.create(CACHE_STRATEGY, ItemCreateCommand("data"))

        val es = Executors.newFixedThreadPool(3)
        val start = System.nanoTime()
        while (System.nanoTime() - start < TimeUnit.SECONDS.toNanos(20)) {
            repeat(3) { es.execute({ ItemApiTestUtils.read(CACHE_STRATEGY, item.itemId) }) }
            TimeUnit.MILLISECONDS.sleep(10)
        }

        ItemApiTestUtils.update(CACHE_STRATEGY, item.itemId, ItemUpdateCommand("updated"))
        val updated = ItemApiTestUtils.read(CACHE_STRATEGY, item.itemId)
        println("updated = $updated")

        ItemApiTestUtils.delete(CACHE_STRATEGY, item.itemId)
        val deleted = ItemApiTestUtils.read(CACHE_STRATEGY, item.itemId)
        println("deleted = $deleted")
    }

    companion object {
        val CACHE_STRATEGY = CacheStrategy.REQUEST_COLLAPSING
    }
}