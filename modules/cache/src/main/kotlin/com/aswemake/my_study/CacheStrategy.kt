package com.aswemake.my_study

enum class CacheStrategy {
    NONE,
    SPRING_CACHE_ANNOTATION,
    // == 이하는 Cache Penetration 현상을 해결하기 위한 전략들 ==
    /**
     * Null Object 패턴은 생성 시점과의 데이터 부정합 문제와 데이터베이스에 존재하지 않는 모든 파라미터에 대한 Null Object를 생성하기에
     * 메모리 낭비 문제를 갖고 있습니다. 때문에 TTL을 짧게하는 것이 필수이며, 다음 상황에 적절합니다
     * - 파라미터 경우의 수에 제한을 둘 수 있는 상황
     * - Null Object까지 메모리 가용량을 확보해둔 상황
     * - 데이터 생성 빈도가 낮아 정합성 오차가 크게 문제되지 않는 상황
     *
     * 추가적으로, 파라미터 입력값 검증 및 Rate Limit과 함께 사용하는 것이 권장됩니다
     */
    NULL_OBJECT_PATTERN,

    /**
     *
     */
    BLOOM_FILTER,

    // == 이하는 Cache Stampede 현상을 해결하기 위한 전략들 ==
    /**
     * 다음과 같은 상황에 사용하자
     * - 동일한 시점에 여러 캐시 데이터가 만료되는 상황
     * - 캐시 만료에 변동성이 허용되는 상황
     *
     * ex) 주기적으로 업데이트되는 통계성 데이터/정기 콘텐츠
     */
    JITTER,
}