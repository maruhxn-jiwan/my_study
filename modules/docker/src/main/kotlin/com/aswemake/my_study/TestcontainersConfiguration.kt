package com.aswemake.my_study

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.mysql.MySQLContainer
import org.testcontainers.utility.DockerImageName

// 테스트 설정에서는 bean 간 호출 최적화 필요 없음 -> 프록시 생성 비용 제거 + startup 속도 개선
@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection // 자동으로 컨테이너 정보를 읽고 datasource 설정을 생성 (원래라면 spring.datasource.url=... 등의 코드가 필요했음)
    fun mysqlContainer(): MySQLContainer {
        return MySQLContainer(DockerImageName.parse("mysql:latest"))
    }

}
