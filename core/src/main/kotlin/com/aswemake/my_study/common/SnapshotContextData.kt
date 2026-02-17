package com.aswemake.my_study.common

data class SnapshotContextData(
    val changeType: String? = null,
    val changeReason: String? = null,
    val sourceSystem: String? = null,
    val clientIp: String? = null,
)
