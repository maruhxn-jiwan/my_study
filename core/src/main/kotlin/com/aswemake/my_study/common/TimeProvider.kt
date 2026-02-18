package com.aswemake.my_study.common

import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class TimeProvider {
    fun getCurrentTime(): LocalDateTime = LocalDateTime.now()
}
