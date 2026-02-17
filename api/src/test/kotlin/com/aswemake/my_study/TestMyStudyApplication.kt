package com.aswemake.my_study

import org.springframework.boot.fromApplication
import org.springframework.boot.with

/**
 * 테스트용 애플리케이션 런처
 * 핵심목적: 테스트 환경 구성 + 컨테이너 포함 실행
 */
fun main(args: Array<String>) {
    fromApplication<MyStudyApplication>()
        .with(TestcontainersConfiguration::class) // 테스트 컨테이너가 컨텍스트에 추가됨 (컨테이너 Bean 같이 로딩)
        .run(*args)
}
