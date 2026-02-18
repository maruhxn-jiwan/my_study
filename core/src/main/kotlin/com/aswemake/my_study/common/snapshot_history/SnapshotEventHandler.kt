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
        @Suppress("UNCHECKED_CAST")
        val entity = event.entity as Snapshotable<SnapshotHistoryEntity>
        val snapshot = entity.toSnapshot(event.context)
        snapshot.open(LocalDateTime.now())
        em.persist(snapshot)
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun handleUpdateOrDelete(event: SnapshotUpdateEvent) {
        @Suppress("UNCHECKED_CAST")
        val entity = event.entity as Snapshotable<SnapshotHistoryEntity>
        val now = LocalDateTime.now()

        @Suppress("UNCHECKED_CAST")
        val historyClass = entity.historyClass() as Class<SnapshotHistoryEntity>
        val openHistories = em.createQuery(
            "SELECT h FROM ${event.historyEntityName} h WHERE h.sourceId = :sourceId AND h.validTo IS NULL",
            historyClass,
        ).setParameter("sourceId", event.sourceEntityId).resultList

        openHistories.forEach { it.close(now) }
        val snapshot = entity.toSnapshot(event.context)
        snapshot.open(now)
        em.persist(snapshot)
    }
}
