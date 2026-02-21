package com.aswemake.my_study.api

import com.aswemake.my_study.CacheStrategy
import com.aswemake.my_study.domain.command.ItemCreateCommand
import com.aswemake.my_study.domain.command.ItemUpdateCommand
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled
class SpringCacheAnnotationStrategyApiTest {
    @Test
    fun createAndReadAndReadAllAndUpdateAndDelete() {
        // create
        val item1 = ItemApiTestUtils.create(
            CACHE_STRATEGY,
            ItemCreateCommand("data1")
        )
        val item2 = ItemApiTestUtils.create(
            CACHE_STRATEGY,
            ItemCreateCommand("data2")
        )
        val item3 = ItemApiTestUtils.create(
            CACHE_STRATEGY,
            ItemCreateCommand("data3")
        )

        // read
        val item1Read1 =
            ItemApiTestUtils.read(CACHE_STRATEGY, item1.itemId)
        val item1Read2 =
            ItemApiTestUtils.read(CACHE_STRATEGY, item1.itemId)
        val item1Read3 =
            ItemApiTestUtils.read(CACHE_STRATEGY, item1.itemId)
        println("item1Read1 = " + item1Read1)
        println("item1Read2 = " + item1Read2)
        println("item1Read3 = " + item1Read3)

        val item2Read1 =
            ItemApiTestUtils.read(CACHE_STRATEGY, item2.itemId)
        val item2Read2 =
            ItemApiTestUtils.read(CACHE_STRATEGY, item2.itemId)
        val item2Read3 =
            ItemApiTestUtils.read(CACHE_STRATEGY, item2.itemId)
        println("item2Read1 = " + item2Read1)
        println("item2Read2 = " + item2Read2)
        println("item2Read3 = " + item2Read3)

        val item3Read1 =
            ItemApiTestUtils.read(CACHE_STRATEGY, item3.itemId)
        val item3Read2 =
            ItemApiTestUtils.read(CACHE_STRATEGY, item3.itemId)
        val item3Read3 =
            ItemApiTestUtils.read(CACHE_STRATEGY, item3.itemId)
        println("item3Read1 = " + item3Read1)
        println("item3Read2 = " + item3Read2)
        println("item3Read3 = " + item3Read3)

        // readAll
        val itemPageReadAll1 =
            ItemApiTestUtils.readAll(CACHE_STRATEGY, 1L, 2L)
        val itemPageReadAll2 =
            ItemApiTestUtils.readAll(CACHE_STRATEGY, 1L, 2L)
        println("itemPageReadAll1 = " + itemPageReadAll1)
        println("itemPageReadAll2 = " + itemPageReadAll2)


        // update
        ItemApiTestUtils.update(
            CACHE_STRATEGY,
            item1.itemId,
            ItemUpdateCommand("updatedData")
        )
        val updated =
            ItemApiTestUtils.read(CACHE_STRATEGY, item1.itemId)
        println("updated = " + updated)

        // delete
        ItemApiTestUtils.delete(CACHE_STRATEGY, item1.itemId)
        try {
            ItemApiTestUtils.read(CACHE_STRATEGY, item1.itemId)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun readNullData() {
        for (i in 0..2) {
            try {
                ItemApiTestUtils.read(CACHE_STRATEGY, 99999)
            } catch (ignored: java.lang.Exception) {
            }
        }
    }

    companion object {
        val CACHE_STRATEGY: CacheStrategy = CacheStrategy.SPRING_CACHE_ANNOTATION
    }
}
