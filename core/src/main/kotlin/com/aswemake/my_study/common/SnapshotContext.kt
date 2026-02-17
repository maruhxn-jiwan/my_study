package com.aswemake.my_study.common

object SnapshotContext {
    private val holder = ThreadLocal<SnapshotContextData>()

    fun set(data: SnapshotContextData) = holder.set(data)

    fun get(): SnapshotContextData? = holder.get()

    fun clear() = holder.remove()
}
