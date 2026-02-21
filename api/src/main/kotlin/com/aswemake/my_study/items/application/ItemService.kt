package com.aswemake.my_study.items.application

import com.aswemake.my_study.CacheStrategy
import com.aswemake.my_study.domain.command.ItemCreateCommand
import com.aswemake.my_study.domain.command.ItemUpdateCommand
import com.aswemake.my_study.service.dto.ItemPageResponse
import com.aswemake.my_study.service.dto.ItemResponse

interface ItemService {
    fun get(itemId: Long): ItemResponse?

    fun getAll(page: Long, size: Long): ItemPageResponse

    fun create(command: ItemCreateCommand): ItemResponse

    fun update(itemId: Long, command: ItemUpdateCommand): ItemResponse

    fun delete(itemId: Long)

    fun supports(cacheStrategy: CacheStrategy): Boolean
}