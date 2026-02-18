package com.aswemake.my_study.common

import com.aswemake.my_study.utils.IntegrationTest
import jakarta.persistence.Entity
import jakarta.persistence.EntityManager
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BaseEntityAuditingTest : IntegrationTest() {

    @Test
    fun `저장 시 createdBy와 updatedBy가 자동으로 설정된다`() {
        val entity = SampleEntity(name = "테스트")

        em.persistAndFlush(entity)

        assertThat(entity.createdBy).isEqualTo("system")
        assertThat(entity.updatedBy).isEqualTo("system")
    }

    @Test
    fun `저장 시 createdAt과 updatedAt이 자동으로 설정된다`() {
        val entity = SampleEntity(name = "테스트")

        em.persistAndFlush(entity)

        assertThat(entity.createdAt).isNotNull()
        assertThat(entity.updatedAt).isNotNull()
    }

    @Test
    fun `수정 시 updatedBy는 변경되지만 createdBy는 변경되지 않는다`() {
        val entity = SampleEntity(name = "원본")
        em.persistAndFlush(entity)

        val originalCreatedBy = entity.createdBy

        entity.name = "수정됨"
        em.persistAndFlush(entity)
        em.clear()

        val found = em.find(SampleEntity::class.java, entity.id)
        assertThat(found.createdBy).isEqualTo(originalCreatedBy)
        assertThat(found.updatedBy).isEqualTo("system")
    }

    @Test
    fun `수정 시 updatedAt은 변경되지만 createdAt은 변경되지 않는다`() {
        val entity = SampleEntity(name = "원본")
        em.persistAndFlush(entity)

        val originalCreatedAt = entity.createdAt

        Thread.sleep(10)
        entity.name = "수정됨"
        em.persistAndFlush(entity)
        em.clear()

        val found = em.find(SampleEntity::class.java, entity.id)
        assertThat(found.createdAt).isEqualTo(originalCreatedAt)
        assertThat(found.updatedAt).isAfterOrEqualTo(found.createdAt)
    }
}

private fun EntityManager.persistAndFlush(entity: SampleEntity) {
    this.persist(entity)
    this.flush()
}

@Entity
@Table(name = "sample_entity")
class SampleEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var name: String = "test"
) : BaseEntity()
