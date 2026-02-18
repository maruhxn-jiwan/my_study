package com.aswemake.my_study.service

import com.aswemake.my_study.TestcontainersConfiguration
import com.aswemake.my_study.common.TimeProvider
import com.aswemake.my_study.domain.UserCreateCommand
import com.aswemake.my_study.infra.UserRepository
import com.aswemake.my_study.utils.MockkBeanConfig
import io.mockk.every
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional
@SpringBootTest
@Import(TestcontainersConfiguration::class, MockkBeanConfig::class)
@TestPropertySource(properties = ["spring.jpa.hibernate.ddl-auto=create-drop"])
class UserWriterTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userWriter: UserWriter

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var timeProvider: TimeProvider

    @BeforeEach
    fun setUp() {
        every { timeProvider.getCurrentTime() } returns LocalDateTime.of(2026, 2, 18, 0, 0)
    }

    @Test
    fun create() {
        val command = UserCreateCommand(name = "user1", email = "user1@test.com")
        val user = userWriter.create(command)

        val savedUser = userRepository.findById(user.userId!!)
        assertThat(savedUser).isEqualTo(user)
    }

    @Test
    fun delete() {
        val command = UserCreateCommand(name = "user1", email = "user1@test.com")
        val user = userWriter.create(command)

        val deletedAt = timeProvider.getCurrentTime()
        user.withdraw(deletedAt)
        em.flush()
        em.clear()

        val savedUser = userRepository.findById(user.userId!!)!!
        assertThat(savedUser.deletedAt).isEqualToIgnoringNanos(deletedAt)

        assertThat(userRepository.findActiveUsers()).isEmpty()
    }
}