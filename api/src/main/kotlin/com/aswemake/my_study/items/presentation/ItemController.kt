package com.aswemake.my_study.items.presentation

import com.aswemake.my_study.CacheStrategy
import com.aswemake.my_study.domain.command.ItemCreateCommand
import com.aswemake.my_study.domain.command.ItemUpdateCommand
import com.aswemake.my_study.items.application.ItemService
import com.aswemake.my_study.service.dto.ItemPageResponse
import com.aswemake.my_study.service.dto.ItemResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ItemController(
    private val itemServices: List<ItemService>
) {
    @GetMapping("/cache-strategy/{cacheStrategy}/items/{itemId}")
    fun read(
        @PathVariable cacheStrategy: CacheStrategy,
        @PathVariable itemId: Long
    ): ItemResponse? {
        return resolveCacheHandler(cacheStrategy).get(itemId)
    }

    @GetMapping("/cache-strategy/{cacheStrategy}/items")
    fun readAll(
        @PathVariable cacheStrategy: CacheStrategy,
        @RequestParam page: Long,
        @RequestParam pageSize: Long
    ): ItemPageResponse {
        return resolveCacheHandler(cacheStrategy).getAll(page, pageSize)
    }

    @PostMapping("/cache-strategy/{cacheStrategy}/items")
    fun create(
        @PathVariable cacheStrategy: CacheStrategy,
        @RequestBody command: ItemCreateCommand
    ): ItemResponse {
        return resolveCacheHandler(cacheStrategy).create(command)
    }

    @PutMapping("/cache-strategy/{cacheStrategy}/items/{itemId}")
    fun update(
        @PathVariable cacheStrategy: CacheStrategy,
        @PathVariable itemId: Long,
        @RequestBody command: ItemUpdateCommand
    ): ItemResponse {
        return resolveCacheHandler(cacheStrategy).update(itemId, command)
    }

    @DeleteMapping("/cache-strategy/{cacheStrategy}/items/{itemId}")
    fun delete(
        @PathVariable cacheStrategy: CacheStrategy,
        @PathVariable itemId: Long
    ) {
        resolveCacheHandler(cacheStrategy).delete(itemId)
    }

    private fun resolveCacheHandler(cacheStrategy: CacheStrategy) =
        itemServices.first() { it.supports(cacheStrategy) }
}