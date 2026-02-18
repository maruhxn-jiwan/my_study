package com.aswemake.my_study.common.snapshot_history

data class SnapshotInsertEvent(
    val entitySnapshot: SnapshotHistoryEntity,
)

data class SnapshotUpdateEvent(
    val entitySnapshot: SnapshotHistoryEntity,
    val historyClazz: Class<*>,
    val historyEntityName: String,
    val sourceEntityId: Long,
)
