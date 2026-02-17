package com.aswemake.my_study.common.snapshot_history

/**
 * 전체 행 스냅샷 이력을 지원하는 엔티티가 구현하는 인터페이스.
 *
 * 사용 방법:
 * 1. 소스 엔티티에 @EntityListeners(SnapshotEntityListener::class) 추가
 * 2. 소스 엔티티에 Snapshotable<H> 구현
 * 3. toSnapshot()에서 this의 필드를 히스토리 엔티티에 매핑
 *
 * SnapshotContext.set(...)로 changeType 등 컨텍스트를 미리 설정하면
 * context 파라미터로 전달된다.
 *
 * 히스토리 엔티티는 반드시 sourceId 필드를 가져야 한다.
 * SnapshotEventHandler가 JPQL로 열린 이력을 조회할 때 해당 필드를 사용한다.
 */
interface Snapshotable<H : SnapshotHistoryEntity> {
    fun toSnapshot(context: SnapshotContextData?): H
}
