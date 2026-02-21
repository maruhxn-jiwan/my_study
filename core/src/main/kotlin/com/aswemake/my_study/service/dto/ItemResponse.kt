package com.aswemake.my_study.service.dto

import com.aswemake.my_study.domain.Item

data class ItemResponse(
    val itemId: Long,
    val data: String,
) {
    companion object {
        fun from(item: Item) = ItemResponse(
            itemId = item.itemId,
            data = item.data
        )
    }
}
