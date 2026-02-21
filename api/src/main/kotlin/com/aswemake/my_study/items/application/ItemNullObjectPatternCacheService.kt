package com.aswemake.my_study.items.application

import com.aswemake.my_study.CacheStrategy
import com.aswemake.my_study.domain.command.ItemCreateCommand
import com.aswemake.my_study.domain.command.ItemUpdateCommand
import com.aswemake.my_study.service.ItemManager
import com.aswemake.my_study.service.dto.ItemResponse
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class ItemNullObjectPatternCacheService(
    private val itemManager: ItemManager
) : ItemService {

    /**
     * 더욱 유연한 구조의 nullObject를 만들 수도 있음
     * 예시) 데이터가 정말 없는 것인지, 또는 비공개 처리되어서 접근이 안되는 것인지 등의 예외 정보도 함께 캐시해서 분기
     */
    companion object {
        private val NULL_OBJECT = ItemResponse(-1, "NULL")
    }

    @Cacheable(cacheNames = ["item"], key = "#itemId")
    override fun get(itemId: Long): ItemResponse {
        return itemManager.get(itemId)
            ?: NULL_OBJECT
    }

    @Cacheable(cacheNames = ["itemList"], key = "#page + ':' + #size")
    override fun getAll(page: Long, size: Long) = itemManager.getAll(page, size)

    override fun create(command: ItemCreateCommand) = itemManager.create(command)

    @CachePut(cacheNames = ["item"], key = "#itemId")
    override fun update(itemId: Long, command: ItemUpdateCommand) = itemManager.update(itemId, command)

    @CacheEvict(cacheNames = ["item"], key = "#itemId")
    override fun delete(itemId: Long) = itemManager.delete(itemId)

    override fun supports(cacheStrategy: CacheStrategy) = CacheStrategy.NULL_OBJECT_PATTERN == cacheStrategy
}