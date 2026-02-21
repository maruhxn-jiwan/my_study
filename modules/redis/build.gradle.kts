dependencies {
    implementation(project(":modules:logging"))
    testImplementation(project(":modules:docker"))

    // AOP
    implementation("org.springframework.boot:spring-boot-starter-aspectj")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.redisson:redisson-spring-boot-starter:4.2.0")
}
