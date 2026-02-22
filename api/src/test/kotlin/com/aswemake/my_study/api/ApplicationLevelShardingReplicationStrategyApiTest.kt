package com.aswemake.my_study.api

import com.aswemake.my_study.CacheStrategy
import com.aswemake.my_study.domain.command.ItemCreateCommand
import com.aswemake.my_study.domain.command.ItemUpdateCommand
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled
class ApplicationLevelShardingReplicationStrategyApiTest {
    companion object {
        val CACHE_STRATEGY = CacheStrategy.APPLICATION_LEVEL_SHARDING_REPLICATION
    }

    @Test
    fun test() {
        val item = ItemApiTestUtils.create(CACHE_STRATEGY, ItemCreateCommand("data"))
        repeat(3) {
            val read = ItemApiTestUtils.read(CACHE_STRATEGY, item.itemId)
            println("read: $read")
        }

        ItemApiTestUtils.update(
            CACHE_STRATEGY,
            item.itemId,
            ItemUpdateCommand("updated")
        )
        val updated = ItemApiTestUtils.read(CACHE_STRATEGY, item.itemId)
        println("updated = $updated")

        ItemApiTestUtils.delete(CACHE_STRATEGY, item.itemId)
        val deleted = ItemApiTestUtils.read(CACHE_STRATEGY, item.itemId)
        println("deleted = $deleted")
    }
}