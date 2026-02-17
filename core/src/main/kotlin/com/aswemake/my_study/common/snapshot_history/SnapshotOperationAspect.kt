package com.aswemake.my_study.common.snapshot_history

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class SnapshotOperationAspect {

    @Around("@annotation(snapshotOperation)")
    fun around(joinPoint: ProceedingJoinPoint, snapshotOperation: SnapshotOperation): Any? {
        val data = SnapshotContextData(
            changeType = snapshotOperation.changeType.ifBlank { null },
            changeReason = snapshotOperation.changeReason.ifBlank { null },
            sourceSystem = snapshotOperation.sourceSystem.ifBlank { null },
            clientIp = snapshotOperation.clientIp.ifBlank { null },
        )
        SnapshotContext.set(data)
        try {
            return joinPoint.proceed()
        } finally {
            SnapshotContext.clear()
        }
    }
}
