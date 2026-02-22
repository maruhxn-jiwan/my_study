package com.aswemake.my_study.items.application

import com.aswemake.my_study.CacheStrategy
import com.aswemake.my_study.annotation.MyCacheEvict
import com.aswemake.my_study.annotation.MyCachePut
import com.aswemake.my_study.annotation.MyCacheable
import com.aswemake.my_study.domain.command.ItemCreateCommand
import com.aswemake.my_study.domain.command.ItemUpdateCommand
import com.aswemake.my_study.service.ItemManager
import com.aswemake.my_study.service.dto.ItemPageResponse
import com.aswemake.my_study.service.dto.ItemResponse
import org.springframework.stereotype.Service

@Service
class ItemRequestCollapsingCacheService(
    private val itemManager: ItemManager,
) : ItemService {

    @MyCacheable(
        strategy = CacheStrategy.REQUEST_COLLAPSING,
        cacheName = "item",
        key = "#itemId",
        ttlSeconds = 1
    )
    override fun get(itemId: Long) = itemManager.get(itemId)

    override fun getAll(page: Long, size: Long): ItemPageResponse = itemManager.getAll(page, size)

    override fun create(command: ItemCreateCommand): ItemResponse = itemManager.create(command)

    @MyCachePut(
        strategy = CacheStrategy.REQUEST_COLLAPSING,
        cacheName = "item",
        key = "#itemId",
        ttlSeconds = 1
    )
    override fun update(
        itemId: Long,
        command: ItemUpdateCommand
    ) = itemManager.update(itemId, command)

    @MyCacheEvict(
        strategy = CacheStrategy.REQUEST_COLLAPSING,
        cacheName = "item",
        key = "#itemId",
    )
    override fun delete(itemId: Long) {
        itemManager.delete(itemId)
    }

    override fun supports(cacheStrategy: CacheStrategy) = CacheStrategy.REQUEST_COLLAPSING == cacheStrategy
}