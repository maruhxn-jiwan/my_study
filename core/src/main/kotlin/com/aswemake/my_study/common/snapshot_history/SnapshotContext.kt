package com.aswemake.my_study.common.snapshot_history

internal object SnapshotContext {
    private val holder = ThreadLocal<SnapshotContextData>()

    fun set(data: SnapshotContextData) = holder.set(data)

    fun get(): SnapshotContextData? = holder.get()

    fun clear() = holder.remove()
}
