package com.aswemake.my_study.common

import com.aswemake.my_study.TestcontainersConfiguration
import com.aswemake.my_study.common.snapshot_history.SnapshotContextData
import com.aswemake.my_study.common.snapshot_history.SnapshotEntityListener
import com.aswemake.my_study.common.snapshot_history.SnapshotHistoryEntity
import com.aswemake.my_study.common.snapshot_history.SnapshotOperation
import com.aswemake.my_study.common.snapshot_history.Snapshotable
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EntityManager
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Service
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate

@SpringBootTest
@Import(TestcontainersConfiguration::class)
@TestPropertySource(properties = ["spring.jpa.hibernate.ddl-auto=create-drop"])
class SnapshotHistoryEntityTest {

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var tx: TransactionTemplate

    @Autowired
    private lateinit var sampleSnapshotService: SampleSnapshotService

    @AfterEach
    fun cleanUp() {
        tx.execute {
            em.createQuery("DELETE FROM SampleSourceHistory h").executeUpdate()
            em.createQuery("DELETE FROM SampleSource s").executeUpdate()
        }
    }

    @Test
    fun `INSERT 시 @PostPersist로 스냅샷이 자동 생성된다`() {
        tx.execute {
            val source = SampleSource(name = "테스트")
            em.persist(source)
            em.flush()
        }

        val histories = tx.execute {
            em.createQuery("SELECT h FROM SampleSourceHistory h", SampleSourceHistory::class.java)
                .resultList
        }!!

        assertThat(histories).hasSize(1)
        assertThat(histories[0].name).isEqualTo("테스트")
    }

    @Test
    fun `INSERT 시 생성된 스냅샷의 validFrom이 설정되고 validTo는 null이다`() {
        tx.execute {
            val source = SampleSource(name = "테스트")
            em.persist(source)
            em.flush()
        }

        val history = tx.execute {
            em.createQuery("SELECT h FROM SampleSourceHistory h", SampleSourceHistory::class.java)
                .singleResult
        }!!

        assertThat(history.validFrom).isNotNull()
        assertThat(history.validTo).isNull()
    }

    @Test
    fun `UPDATE 시 @PostUpdate로 스냅샷이 추가 생성된다`() {
        val sourceId = tx.execute {
            val source = SampleSource(name = "원본")
            em.persist(source)
            em.flush()
            source.id
        }!!

        tx.execute {
            val source = em.find(SampleSource::class.java, sourceId)
            source.name = "수정됨"
            em.flush()
        }

        val histories = tx.execute {
            em.createQuery(
                "SELECT h FROM SampleSourceHistory h ORDER BY h.snapshotAt ASC",
                SampleSourceHistory::class.java
            )
                .resultList
        }!!

        assertThat(histories).hasSize(2)
        assertThat(histories[0].name).isEqualTo("원본")
        assertThat(histories[1].name).isEqualTo("수정됨")
    }

    @Test
    fun `UPDATE 시 이전 히스토리의 validTo가 닫히고 새 히스토리의 validFrom이 설정된다`() {
        val sourceId = tx.execute {
            val source = SampleSource(name = "원본")
            em.persist(source)
            em.flush()
            source.id
        }!!

        tx.execute {
            val source = em.find(SampleSource::class.java, sourceId)
            source.name = "수정됨"
            em.flush()
        }

        val histories = tx.execute {
            em.createQuery(
                "SELECT h FROM SampleSourceHistory h ORDER BY h.snapshotAt ASC",
                SampleSourceHistory::class.java
            ).resultList
        }!!

        val first = histories[0]
        val second = histories[1]

        // 첫 번째 히스토리(원본)는 validTo가 닫혀 있어야 한다
        assertThat(first.validTo).isNotNull()
        // 두 번째 히스토리(수정됨)는 validFrom이 설정되고 validTo는 열려 있어야 한다
        assertThat(second.validFrom).isNotNull()
        assertThat(second.validTo).isNull()
        // 첫 번째 validTo와 두 번째 validFrom은 같은 시각이어야 한다
        assertThat(first.validTo).isEqualTo(second.validFrom)
    }

    @Test
    fun `DELETE 시 이전 히스토리의 validTo가 닫히고 삭제 시점 스냅샷이 생성된다`() {
        val sourceId = tx.execute {
            val source = SampleSource(name = "삭제될 엔티티")
            em.persist(source)
            em.flush()
            source.id
        }!!

        tx.execute {
            val source = em.find(SampleSource::class.java, sourceId)
            em.remove(source)
            em.flush()
        }

        val histories = tx.execute {
            em.createQuery(
                "SELECT h FROM SampleSourceHistory h ORDER BY h.snapshotAt ASC",
                SampleSourceHistory::class.java
            ).resultList
        }!!

        assertThat(histories).hasSize(2)

        val first = histories[0]
        val second = histories[1]

        // 첫 번째 히스토리(INSERT 시점)는 validTo가 닫혀 있어야 한다
        assertThat(first.validTo).isNotNull()
        // 두 번째 히스토리(DELETE 시점)는 validFrom이 설정되고 validTo는 null이어야 한다
        assertThat(second.validFrom).isNotNull()
        assertThat(second.validTo).isNull()
        assertThat(first.validTo).isEqualTo(second.validFrom)
    }

    @Test
    fun `SnapshotContext 설정 시 changeType이 스냅샷에 포함된다`() {
        sampleSnapshotService.createSource("컨텍스트 테스트")

        val history = tx.execute {
            em.createQuery("SELECT h FROM SampleSourceHistory h", SampleSourceHistory::class.java)
                .singleResult
        }!!

        assertThat(history.changeType).isEqualTo("PRICE_CHANGE")
        assertThat(history.changeReason).isEqualTo("정기 조정")
    }

    @Test
    fun `SnapshotContext 미설정 시 changeType이 null이다`() {
        tx.execute {
            val source = SampleSource(name = "컨텍스트 없음")
            em.persist(source)
            em.flush()
        }

        val history = tx.execute {
            em.createQuery("SELECT h FROM SampleSourceHistory h", SampleSourceHistory::class.java)
                .singleResult
        }!!

        assertThat(history.changeType).isNull()
        assertThat(history.changeReason).isNull()
    }
}

// ── 테스트 전용 서비스 ─────────────────────────────────────────

@Service
class SampleSnapshotService(private val em: EntityManager) {

    @Transactional
    @SnapshotOperation(changeType = "PRICE_CHANGE", changeReason = "정기 조정")
    fun createSource(name: String) {
        val source = SampleSource(name = name)
        em.persist(source)
        em.flush()
    }
}

// ── 테스트 전용 엔티티 ─────────────────────────────────────────

@Entity
@Table(name = "sample_source")
@EntityListeners(SnapshotEntityListener::class)
class SampleSource(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column
    var name: String,
) : Snapshotable<SampleSourceHistory> {
    override fun toSnapshot(context: SnapshotContextData?) = SampleSourceHistory(
        sourceId = this.id,
        name = this.name,
        context = context,
    )
}

@Entity
@Table(name = "sample_source_history")
class SampleSourceHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "source_id")
    val sourceId: Long?,

    @Column
    val name: String,

    context: SnapshotContextData? = null,
) : SnapshotHistoryEntity(
    changeType = context?.changeType,
    changeReason = context?.changeReason,
    sourceSystem = context?.sourceSystem,
    clientIp = context?.clientIp,
)
