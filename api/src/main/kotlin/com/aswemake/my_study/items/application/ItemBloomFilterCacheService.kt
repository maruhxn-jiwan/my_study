package com.aswemake.my_study.items.application

import com.aswemake.my_study.CacheStrategy
import com.aswemake.my_study.bloom_filter.BloomFilter
import com.aswemake.my_study.bloom_filter.BloomFilterRedisHandler
import com.aswemake.my_study.domain.command.ItemCreateCommand
import com.aswemake.my_study.domain.command.ItemUpdateCommand
import com.aswemake.my_study.service.ItemManager
import com.aswemake.my_study.service.dto.ItemResponse
import org.springframework.stereotype.Service

@Service
class ItemBloomFilterCacheService(
    private val itemManager: ItemManager,
    private val bloomFilterRedisHandler: BloomFilterRedisHandler
) : ItemService {

    companion object {
        private val bloomFilter = BloomFilter.create(
            id = "item-bloom-filter",
            dataCount = 1000,
            falsePositiveRate = 0.01
        )
    }

    override fun get(itemId: Long): ItemResponse? {
        val result = bloomFilterRedisHandler.mightContain(bloomFilter, itemId.toString())
        if (!result) {
            return null
        }
        return itemManager.get(itemId)
    }

    override fun getAll(page: Long, size: Long) = itemManager.getAll(page, size)

    override fun create(command: ItemCreateCommand): ItemResponse {
        val itemResponse = itemManager.create(command)
        bloomFilterRedisHandler.add(bloomFilter, itemResponse.itemId.toString())
        return itemResponse
    }

    override fun update(itemId: Long, command: ItemUpdateCommand) = itemManager.update(itemId, command)

    override fun delete(itemId: Long) = itemManager.delete(itemId)

    override fun supports(cacheStrategy: CacheStrategy) = CacheStrategy.BLOOM_FILTER == cacheStrategy
}