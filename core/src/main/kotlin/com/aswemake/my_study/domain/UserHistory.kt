package com.aswemake.my_study.domain

import com.aswemake.my_study.common.snapshot_history.SnapshotContextData
import com.aswemake.my_study.common.snapshot_history.SnapshotHistoryEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "user_history")
class UserHistory(
    override var sourceId: Long,
    context: SnapshotContextData? = null,

    @Column(name = "name")
    val name: String,

    @Column(name = "email")
    val email: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    val historyId: Long? = null
) : SnapshotHistoryEntity(
    sourceId = sourceId,
    changeType = context?.changeType,
    changeReason = context?.changeReason,
    sourceSystem = context?.sourceSystem,
    clientIp = context?.clientIp,
)