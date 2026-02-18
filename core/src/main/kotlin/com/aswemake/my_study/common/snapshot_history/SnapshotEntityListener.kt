package com.aswemake.my_study.common.snapshot_history

import jakarta.persistence.Entity
import jakarta.persistence.PostPersist
import jakarta.persistence.PostRemove
import jakarta.persistence.PostUpdate
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class SnapshotEntityListener(
    private val publisher: ApplicationEventPublisher
) {

    @PostPersist
    fun onInsert(entity: Any) {
        if (entity !is Snapshotable<*>) return
        val context = SnapshotContext.get()
        publisher.publishEvent(SnapshotInsertEvent(entity.toSnapshot(context)))
    }

    @PostUpdate
    @PostRemove
    fun onUpdateOrRemove(entity: Any) {
        if (entity !is Snapshotable<*>) return

        val context = SnapshotContext.get()
        val cls = entity.historyClass()
        val entityName = cls.getAnnotation(Entity::class.java)?.name
            ?.takeIf { it.isNotEmpty() } ?: cls.simpleName

        publisher.publishEvent(
            SnapshotUpdateEvent(
                entitySnapshot = entity.toSnapshot(context),
                historyClazz = entity.historyClass(),
                historyEntityName = entityName,
                sourceEntityId = entity.entityId()
            )
        )
    }
}