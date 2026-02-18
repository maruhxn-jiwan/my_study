package com.aswemake.my_study.infra

import com.aswemake.my_study.domain.User
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository

interface UserRepository : Repository<User, Long> {
    fun findById(userId: Long): User?

    fun save(user: User): User

    @Query("SELECT u from User u where u.deletedAt IS NULL")
    fun findActiveUsers(): List<User>
}