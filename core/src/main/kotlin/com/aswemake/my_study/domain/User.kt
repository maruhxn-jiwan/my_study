package com.aswemake.my_study.domain

import com.aswemake.my_study.common.snapshot_history.SnapshotContextData
import com.aswemake.my_study.common.snapshot_history.SnapshotEntityListener
import com.aswemake.my_study.common.snapshot_history.Snapshotable
import com.aswemake.my_study.domain.command.UserCreateCommand
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "users")
@EntityListeners(SnapshotEntityListener::class)
class User(
    @Column(name = "name")
    var name: String,

    @Column(name = "email")
    val email: String,

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val userId: Long? = null,
) : Snapshotable<UserHistory> {

    fun updateName(name: String) {
        this.name = name
    }

    fun withdraw(deletedAt: LocalDateTime) {
        this.deletedAt = deletedAt
    }

    companion object {
        fun create(userCreateCommand: UserCreateCommand): User {
            return User(userCreateCommand.name, userCreateCommand.email)
        }
    }

    // --- 스냅샷 헬퍼 메서드 ---
    override fun toSnapshot(context: SnapshotContextData?) = UserHistory(
        name = name,
        email = email,
        sourceId = entityId(),
        context = context
    )

    override fun historyClass() = UserHistory::class.java

    override fun entityId() = userId!!
}