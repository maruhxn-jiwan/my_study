package com.aswemake.my_study.common.snapshot_history

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
        val publisher = SnapshotEntityListenerSupport.eventPublisher ?: return
        publisher.publishEvent(SnapshotInsertEvent(entity, SnapshotContext.get()))
    }

    @PostUpdate
    @PostRemove
    fun onUpdateOrRemove(entity: Any) {
        if (entity !is Snapshotable<*>) return
        val publisher = SnapshotEntityListenerSupport.eventPublisher ?: return
        publisher.publishEvent(SnapshotUpdateEvent(entity, SnapshotContext.get()))
    }
}

object SnapshotEntityListenerSupport {
    var eventPublisher: ApplicationEventPublisher? = null
}
