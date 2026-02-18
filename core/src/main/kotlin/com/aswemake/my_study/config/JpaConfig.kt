package com.aswemake.my_study.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import java.util.Optional

@Configuration
@EnableJpaAuditing
class JpaConfig() {

    /**
     * 스프링 시큐리티 사용 시, SecurityContextHolder에서 사용자 정보 뽑아오기
     */
    @Bean
    fun auditorAware(): AuditorAware<String> = AuditorAware {
        Optional.ofNullable("system")
    }

    @Bean
    fun transactionTemplate(txManager: PlatformTransactionManager) = TransactionTemplate(txManager)
}
