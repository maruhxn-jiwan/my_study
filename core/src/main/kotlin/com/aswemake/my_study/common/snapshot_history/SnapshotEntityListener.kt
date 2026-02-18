package com.aswemake.my_study.common.snapshot_history

import jakarta.persistence.Entity
import jakarta.persistence.PostPersist
import jakarta.persistence.PostRemove
import jakarta.persistence.PostUpdate
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class SnapshotEntityListener {

    @PostPersist
    fun onInsert(entity: Any) {
        if (entity !is Snapshotable<*>) return
        val publisher = SnapshotEntityListenerSupport.eventPublisher
        publisher.publishEvent(SnapshotInsertEvent(entity, SnapshotContext.get()))
    }

    @PostUpdate
    @PostRemove
    fun onUpdateOrRemove(entity: Any) {
        if (entity !is Snapshotable<*>) return
        val publisher = SnapshotEntityListenerSupport.eventPublisher
        val cls = entity.historyClass()
        val entityName = cls.getAnnotation(Entity::class.java)?.name
            ?.takeIf { it.isNotEmpty() } ?: cls.simpleName
        publisher.publishEvent(SnapshotUpdateEvent(entity, SnapshotContext.get(), entityName, entity.entityId()))
    }
}

object SnapshotEntityListenerSupport {
    lateinit var eventPublisher: ApplicationEventPublisher
}
