package com.aswemake.my_study.utils

import com.aswemake.my_study.TestcontainersConfiguration
import io.mockk.clearAllMocks
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@Import(MockkBeanConfig::class, TestcontainersConfiguration::class)
@TestPropertySource(properties = ["spring.jpa.hibernate.ddl-auto=create-drop"])
class IntegrationTest {

    @Autowired
    lateinit var em: EntityManager

    @BeforeEach
    fun resetMocks() {
        clearAllMocks()
    }
}