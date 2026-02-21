package com.aswemake.my_study

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

// 테스트 설정에서는 bean 간 호출 최적화 필요 없음 -> 프록시 생성 비용 제거 + startup 속도 개선
@TestConfiguration(proxyBeanMethods = false)
class RedisTestcontainersConfiguration {

    @Bean
    @ServiceConnection(name = "redis") // 자동으로 컨테이너 정보를 읽고 spring.data.redis 설정을 생성
    fun redisContainer(): GenericContainer<*> {
        return GenericContainer(DockerImageName.parse("redis:latest"))
            .withExposedPorts(6379)
    }

}
