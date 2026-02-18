package com.aswemake.my_study.common.snapshot_history

import com.aswemake.my_study.common.TimeProvider
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class SnapshotEventHandler(
    private val em: EntityManager,
    private val timeProvider: TimeProvider
) {

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun handleInsert(event: SnapshotInsertEvent) {
        val now = timeProvider.getCurrentTime()

        val snapshot = event.entitySnapshot
        snapshot.open(now)
        em.persist(snapshot)
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun handleUpdateOrDelete(event: SnapshotUpdateEvent) {
        val now = timeProvider.getCurrentTime()
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
