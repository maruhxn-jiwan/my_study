package com.aswemake.my_study.common.snapshot_history

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class SnapshotHistoryEntity(
    @CreatedDate
    @Column(name = "snapshot_at", updatable = false)
    var snapshotAt: LocalDateTime? = null,

    @Column(name = "valid_from")
    var validFrom: LocalDateTime? = null,

    /**
     * 데이터가 변경 및 삭제될 경우, validTo가 현재 시각으로 설정되어야 함
     */
    @Column(name = "valid_to")
    var validTo: LocalDateTime? = null,

    // 변경 사유 컬럼
    @Column(name = "change_type", length = 50, updatable = false)
    val changeType: String? = null,

    @Column(name = "change_reason", length = 500, updatable = false)
    val changeReason: String? = null,

    // 감사(Audit) 컬럼
    @Column(name = "source_system", length = 50, updatable = false)
    val sourceSystem: String? = null,

    @Column(name = "client_ip", length = 50, updatable = false)
    val clientIp: String? = null,
) {
    fun open(from: LocalDateTime) {
        this.validFrom = from
    }

    fun close(to: LocalDateTime) {
        this.validTo = to
    }
}