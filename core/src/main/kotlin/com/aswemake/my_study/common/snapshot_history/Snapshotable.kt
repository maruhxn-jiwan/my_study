package com.aswemake.my_study.common.snapshot_history

/**
 * 전체 행 스냅샷 이력을 지원하는 엔티티가 구현하는 인터페이스.
 *
 * 사용 방법:
 * 1. 소스 엔티티에 @EntityListeners(SnapshotEntityListener::class) 추가
 * 2. 소스 엔티티에 Snapshotable<H> 구현
 * 3. toSnapshot()에서 this의 필드를 히스토리 엔티티에 매핑
 * 4. historyClass()에서 구체 히스토리 엔티티 클래스 반환
 * 5. entityId()에서 소스 엔티티의 PK 반환
 *
 * SnapshotContext.set(...)로 changeType 등 컨텍스트를 미리 설정하면
 * context 파라미터로 전달된다.
 */
interface Snapshotable<H : SnapshotHistoryEntity> {
    fun toSnapshot(context: SnapshotContextData?): H
    fun historyClass(): Class<H>
    fun entityId(): Long
}
