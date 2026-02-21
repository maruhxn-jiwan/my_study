package com.aswemake.my_study.service.dto

import com.aswemake.my_study.domain.Item

data class ItemPageResponse(
    val items: List<ItemResponse>,
    val count: Long
) {
    companion object {
        fun from(items: List<Item>, count: Long): ItemPageResponse {
            return ItemPageResponse(
                items = items.map { ItemResponse.from(it) },
                count = count
            )
        }
    }
}
