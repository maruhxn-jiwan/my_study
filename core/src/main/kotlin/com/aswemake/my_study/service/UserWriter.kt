package com.aswemake.my_study.service

import com.aswemake.my_study.common.DomainService
import com.aswemake.my_study.common.TimeProvider
import com.aswemake.my_study.common.snapshot_history.SnapshotOperation
import com.aswemake.my_study.domain.User
import com.aswemake.my_study.domain.command.UserCreateCommand
import com.aswemake.my_study.infra.UserRepository
import org.springframework.transaction.annotation.Transactional

@DomainService
class UserWriter(
    private val userRepository: UserRepository,
    private val timeProvider: TimeProvider
) {

    @Transactional
    @SnapshotOperation(changeType = "CREATE", changeReason = "회원가입")
    fun create(userCreateCommand: UserCreateCommand): User {
        val user = User.create(userCreateCommand)
        return userRepository.save(user)
    }

    @Transactional
    @SnapshotOperation(changeType = "PROFILE_UPDATE", changeReason = "프로필 업데이트")
    fun update(userId: Long, updatedName: String) {
        val user = userRepository.findById(userId)
            ?: throw NoSuchElementException("유저 정보가 존재하지 않습니다. userId: $userId")

        user.updateName(updatedName)
    }

    @Transactional
    @SnapshotOperation(changeType = "WITHDRAW", changeReason = "회원 탈퇴")
    fun withdraw(userId: Long, updatedName: String) {
        val now = timeProvider.getCurrentTime()
        val user = userRepository.findById(userId)
            ?: throw NoSuchElementException("유저 정보가 존재하지 않습니다. userId: $userId")

        user.withdraw(now)
    }
}