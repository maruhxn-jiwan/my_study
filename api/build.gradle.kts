plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":modules:docker"))
    implementation(project(":modules:cache"))

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("tools.jackson.module:jackson-module-kotlin")

    testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
}
