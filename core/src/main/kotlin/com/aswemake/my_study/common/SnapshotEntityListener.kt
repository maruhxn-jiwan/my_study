package com.aswemake.my_study.common

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.PostPersist
import jakarta.persistence.PostUpdate
import org.springframework.stereotype.Component

@Component
class SnapshotEntityListener {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @PostPersist
    @PostUpdate
    fun onLifecycleEvent(entity: Any) {
        if (entity !is Snapshotable<*>) return

        @Suppress("UNCHECKED_CAST")
        val snapshotable = entity as Snapshotable<SnapshotHistoryEntity>
        val snapshot = snapshotable.toSnapshot(SnapshotContext.get())
        entityManager.persist(snapshot)
    }
}
