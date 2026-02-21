package com.aswemake.my_study.service

import com.aswemake.my_study.IntegrationTest
import com.aswemake.my_study.domain.command.UserCreateCommand
import com.aswemake.my_study.infra.UserRepository
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class UserWriterTest(
    private val userRepository: UserRepository,
    private val userWriter: UserWriter
) : IntegrationTest() {

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