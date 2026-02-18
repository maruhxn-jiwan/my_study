package com.aswemake.my_study

import com.aswemake.my_study.common.TimeProvider
import com.ninjasquad.springmockk.MockkBean
import io.mockk.clearAllMocks
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest(classes = [IntegrationTest.Config::class])
@Import(TestcontainersConfiguration::class)
@TestPropertySource(properties = ["spring.jpa.hibernate.ddl-auto=create-drop"])
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class IntegrationTest {

    @SpringBootApplication
    class Config

    @Autowired
    protected lateinit var em: EntityManager

    @MockkBean
    protected lateinit var timeProvider: TimeProvider

    @BeforeEach
    protected fun resetMocks() {
        clearAllMocks()
    }
}
