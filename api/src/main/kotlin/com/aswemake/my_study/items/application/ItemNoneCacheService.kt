package com.aswemake.my_study.items.application

import com.aswemake.my_study.CacheStrategy
import com.aswemake.my_study.annotation.MyCacheEvict
import com.aswemake.my_study.annotation.MyCachePut
import com.aswemake.my_study.annotation.MyCacheable
import com.aswemake.my_study.domain.command.ItemCreateCommand
import com.aswemake.my_study.domain.command.ItemUpdateCommand
import com.aswemake.my_study.service.ItemManager
import org.springframework.stereotype.Service

@Service
class ItemNoneCacheService(
    private val itemManager: ItemManager
) : ItemService {
    @MyCacheable(
        strategy = CacheStrategy.NONE,
        cacheName = "item",
        key = "#itemId",
        ttlSeconds = 5
    )
    override fun get(itemId: Long) = itemManager.get(itemId)

    @MyCacheable(
        strategy = CacheStrategy.NONE,
        cacheName = "itemList",
        key = "#page + ':' + #pageSize",
        ttlSeconds = 5
    )
    override fun getAll(page: Long, size: Long) = itemManager.getAll(page, size)

    override fun create(command: ItemCreateCommand) = itemManager.create(command)

    @MyCachePut(
        strategy = CacheStrategy.NONE,
        cacheName = "item",
        key = "#itemId",
        ttlSeconds = 5
    )
    override fun update(itemId: Long, command: ItemUpdateCommand) = itemManager.update(itemId, command)

    @MyCacheEvict(
        strategy = CacheStrategy.NONE,
        cacheName = "item",
        key = "#itemId",
    )
    override fun delete(itemId: Long) = itemManager.delete(itemId)

    override fun supports(cacheStrategy: CacheStrategy) = CacheStrategy.NONE == cacheStrategy
}