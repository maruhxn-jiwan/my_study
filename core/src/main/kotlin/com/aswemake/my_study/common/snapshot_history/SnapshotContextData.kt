package com.aswemake.my_study.common.snapshot_history

import java.time.LocalDateTime

data class SnapshotContextData(
    val validFrom: LocalDateTime? = null,
    val validTo: LocalDateTime? = null,
    val changeType: String? = null,
    val changeReason: String? = null,
    val sourceSystem: String? = null,
    val clientIp: String? = null,
)
