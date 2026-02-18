package com.aswemake.my_study.common.snapshot_history

data class SnapshotInsertEvent(
    val entity: Snapshotable<*>,
    val context: SnapshotContextData?,
)

data class SnapshotUpdateEvent(
    val entity: Snapshotable<*>,
    val context: SnapshotContextData?,
    val historyEntityName: String,
    val sourceEntityId: Long,
)
