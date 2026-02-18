package com.aswemake.my_study.utils

import com.aswemake.my_study.common.TimeProvider
import io.mockk.mockk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class MockkBeanConfig {
    @Bean
    fun timeProvider(): TimeProvider = mockk(relaxed = true)
}