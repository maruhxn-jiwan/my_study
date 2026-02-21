package com.aswemake.my_study.infra

import com.aswemake.my_study.domain.Item
import logger
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentSkipListMap

@Repository
class ItemRepository(
    private val db: ConcurrentSkipListMap<Long, Item> = ConcurrentSkipListMap(Comparator.reverseOrder())
) {
    private val log = logger<ItemRepository>()

    fun findById(itemId: Long): Item? {
        log.info("[ItemRepository.find] itemId: $itemId")
        return db.get(itemId)
    }

    fun findAll(page: Long, pageSize: Long): List<Item> {
        log.info("[ItemRepository.findAll] page: $page, pageSize: $pageSize")
        return db.values.stream()
            .skip((page - 1) * pageSize)
            .limit(pageSize)
            .toList()
    }

    fun create(item: Item): Item {
        log.info("[ItemRepository.save] item: $item")
        db[item.itemId] = item
        return item
    }

    fun update(item: Item): Item {
        log.info("[ItemRepository.update] item: $item")
        db[item.itemId] = item
        return item
    }

    fun delete(item: Item) {
        log.info("[ItemRepository.delete] item: $item")
        db.remove(item.itemId)
    }

    fun count(): Long {
        log.info("[ItemRepository.count]")
        return db.size.toLong()
    }
}