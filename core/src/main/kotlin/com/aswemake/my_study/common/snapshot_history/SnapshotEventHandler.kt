package com.aswemake.my_study.common.snapshot_history

import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import java.time.LocalDateTime

@Component
class SnapshotEventHandler(
    private val em: EntityManager,
    private val emf: EntityManagerFactory,
) {

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

        val snapshot = entity.toSnapshot(event.context)

        @Suppress("UNCHECKED_CAST")
        val historyClass = snapshot.javaClass as Class<SnapshotHistoryEntity>
        val entityName = em.metamodel.entity(historyClass).name
        val sourceId = emf.persistenceUnitUtil.getIdentifier(event.entity)

        val openHistories = em.createQuery(
            "SELECT h FROM $entityName h WHERE h.sourceId = :sourceId AND h.validTo IS NULL",
            historyClass,
        ).setParameter("sourceId", sourceId).resultList

        openHistories.forEach { it.close(now) }
        snapshot.open(now)
        em.persist(snapshot)
    }
}
