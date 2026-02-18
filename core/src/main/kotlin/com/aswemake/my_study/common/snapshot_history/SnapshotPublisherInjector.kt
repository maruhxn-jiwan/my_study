package com.aswemake.my_study.common.snapshot_history

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

/**
 * Spring이
 */
@Component
class SnapshotPublisherInjector(
    publisher: ApplicationEventPublisher
) {
    init {
        SnapshotEntityListenerSupport.eventPublisher = publisher
    }
}