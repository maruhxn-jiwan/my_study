package com.aswemake.my_study.service

import com.aswemake.my_study.common.DomainService
import com.aswemake.my_study.domain.Item
import com.aswemake.my_study.domain.command.ItemCreateCommand
import com.aswemake.my_study.domain.command.ItemUpdateCommand
import com.aswemake.my_study.infra.ItemRepository
import com.aswemake.my_study.service.dto.ItemPageResponse
import com.aswemake.my_study.service.dto.ItemResponse

/**
 * DB와 직접 통신하는 도메인 서비스
 */
@DomainService
class ItemManager(
    private val itemRepository: ItemRepository
) {
    fun get(itemId: Long): ItemResponse? {
        val item = itemRepository.findById(itemId)
            ?: return null
        return ItemResponse.from(item)
    }

    fun getAll(page: Long, size: Long) = ItemPageResponse.from(
        items = itemRepository.findAll(page, size),
        count = itemRepository.count()
    )

    fun create(command: ItemCreateCommand): ItemResponse {
        val item = Item.create(command)
        return ItemResponse.from(itemRepository.create(item))
    }

    fun update(itemId: Long, command: ItemUpdateCommand): ItemResponse {
        val item = (itemRepository.findById(itemId)
            ?: throw NoSuchElementException())
        item.update(command)
        return ItemResponse.from(itemRepository.update(item))
    }

    fun delete(itemId: Long) {
        itemRepository.findById(itemId)?.let { itemRepository.delete(it) }
    }

    fun count() = itemRepository.count()
}