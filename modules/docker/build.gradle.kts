dependencies {
    // @TestConfiguration 어노테이션 컴파일 시 필요 (main 소스에서 사용)
    compileOnly("org.springframework.boot:spring-boot-starter-test")

    // 로컬 개발용 Docker Compose 자동 실행 지원
    implementation("org.springframework.boot:spring-boot-docker-compose")

    // Testcontainers (테스트 인프라 유틸 main에 배치 → 다른 모듈이 testImplementation으로 재사용)
    implementation("org.springframework.boot:spring-boot-testcontainers")
    implementation("org.testcontainers:testcontainers-junit-jupiter")
    implementation("org.testcontainers:testcontainers-mysql")
}
