package com.aswemake.my_study.items.application

import com.aswemake.my_study.CacheStrategy
import com.aswemake.my_study.domain.command.ItemCreateCommand
import com.aswemake.my_study.domain.command.ItemUpdateCommand
import com.aswemake.my_study.service.ItemManager
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class ItemSpringCacheAnnotationCacheService(
    private val itemManager: ItemManager
) : ItemService {
    /**
     * condition 조건에 서킷 상태 체크 로직 추가
     */
    @Cacheable(
        cacheNames = ["item"],
        key = "#itemId",
        unless = "#result == null",
        condition = "#p0 != null && @cacheStateProvider.isCacheEnabled()"
    )
    override fun get(itemId: Long) = itemManager.get(itemId)

    @Cacheable(cacheNames = ["itemList"], key = "#page + ':' + #size")
    override fun getAll(page: Long, size: Long) = itemManager.getAll(page, size)

    /**
     * 생성 시점에 즉시 캐시를 갱신할 수도 있으나, 즉시 접근되지 않는 데이터라면 조회 시점에 캐시를 만들어줘도 충분하다.
     */
    override fun create(command: ItemCreateCommand) = itemManager.create(command)

    /**
     * 즉시 접근되지 않는 데이터라면 조회 시점에 캐시를 만들어줘도 충분하다.
     */
    @CachePut(cacheNames = ["item"], key = "#itemId")
    override fun update(itemId: Long, command: ItemUpdateCommand) = itemManager.update(itemId, command)

    @CacheEvict(cacheNames = ["item"], key = "#itemId")
    override fun delete(itemId: Long) = itemManager.delete(itemId)

    override fun supports(cacheStrategy: CacheStrategy) = CacheStrategy.SPRING_CACHE_ANNOTATION == cacheStrategy
}