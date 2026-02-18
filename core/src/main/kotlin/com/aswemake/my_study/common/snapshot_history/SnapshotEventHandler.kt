package com.aswemake.my_study.common.snapshot_history

import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import java.time.LocalDateTime

@Component
class SnapshotEventHandler(private val em: EntityManager) {

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun handleInsert(event: SnapshotInsertEvent) {
        val now = LocalDateTime.now()

        val snapshot = event.entitySnapshot
        snapshot.open(now)
        em.persist(snapshot)
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun handleUpdateOrDelete(event: SnapshotUpdateEvent) {
        val now = LocalDateTime.now()
        val openHistories = em.createQuery(
            "SELECT h FROM ${event.historyEntityName} h WHERE h.sourceId = :sourceId AND h.validTo IS NULL",
            event.historyClazz,
        ).setParameter("sourceId", event.sourceEntityId).resultList as List<SnapshotHistoryEntity>

        openHistories.forEach { it.close(now) }
        val snapshot = event.entitySnapshot
        snapshot.open(now)
        em.persist(snapshot)
    }
}
