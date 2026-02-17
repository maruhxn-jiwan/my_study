package com.aswemake.my_study.common.snapshot_history

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SnapshotOperation(
    val changeType: String = "",
    val changeReason: String = "",
    val sourceSystem: String = "",
    val clientIp: String = "",
)
