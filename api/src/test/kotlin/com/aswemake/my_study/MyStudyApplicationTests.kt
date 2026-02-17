package com.aswemake.my_study

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@Import(TestcontainersConfiguration::class) // 명시적으로 등록
@SpringBootTest
class MyStudyApplicationTests {

    @Test
    fun contextLoads() {
    }

}
