package com.aswemake.my_study.domain

import com.aswemake.my_study.domain.command.ItemCreateCommand
import com.aswemake.my_study.domain.command.ItemUpdateCommand
import java.util.concurrent.atomic.AtomicLong

class Item(
    var data: String,
    val itemId: Long = 0L
) {

    fun update(command: ItemUpdateCommand) {
        this.data = command.data
    }

    companion object {
        private val NEXT_ID = AtomicLong()

        fun create(command: ItemCreateCommand) = Item(
            itemId = NEXT_ID.getAndIncrement(),
            data = command.data
        )
    }
}