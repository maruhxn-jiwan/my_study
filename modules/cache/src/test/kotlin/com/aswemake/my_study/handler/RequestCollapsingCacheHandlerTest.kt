package com.aswemake.my_study.handler

import com.aswemake.my_study.RedisTestContainerSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class RequestCollapsingCacheHandlerTest : RedisTestContainerSupport() {
    @Autowired
    private lateinit var requestCollapsingCacheHandler: RequestCollapsingCacheHandler

    @Test
    fun put() {
        requestCollapsingCacheHandler.put("test", Duration.ofSeconds(10), "data")

        val result = redisTemplate.opsForValue().get("test")
        assertThat(result).isNotNull
        println("result = $result")
    }

    @Test
    fun `여러 동시 요청이 오더라도, DB에는 1회 접근한다`() {
        val es = Executors.newFixedThreadPool(10)
        val latch = CountDownLatch(10)
        val dataSourceExecCount = AtomicInteger(0)

        repeat(10) {
            es.execute {
                val result = requestCollapsingCacheHandler.fetch(
                    key = "test",
                    ttl = Duration.ofSeconds(10),
                    dataSourceSupplier = {
                        try {
                            TimeUnit.SECONDS.sleep(1)
                        } catch (ignored: InterruptedException) {
                        }
                        dataSourceExecCount.incrementAndGet()
                        return@fetch "sourceData"
                    },
                    clazz = String::class.java,
                )
                println("result = $result")
                assertThat(result).isEqualTo("sourceData")
                latch.countDown()
            }
        }

        latch.await()

        assertThat(dataSourceExecCount.get()).isOne
    }

    @Test
    fun `여러 동시 요청이 오는 상황에서, 타임아웃이 발생하면 refresh 된다`() {
        val es = Executors.newFixedThreadPool(10)
        val latch = CountDownLatch(10)
        val dataSourceExecCount = AtomicInteger(0)

        repeat(10) {
            es.execute {
                val result = requestCollapsingCacheHandler.fetch(
                    key = "test",
                    ttl = Duration.ofSeconds(10),
                    dataSourceSupplier = {
                        try {
                            // 타임아웃 시간인 2초보다 긴 시각 -> 모든 요청이 refresh 호출
                            TimeUnit.SECONDS.sleep(3)
                        } catch (ignored: InterruptedException) {
                        }
                        dataSourceExecCount.incrementAndGet()
                        return@fetch "sourceData"
                    },
                    clazz = String::class.java,
                )
                println("result = $result")
                assertThat(result).isEqualTo("sourceData")
                latch.countDown()
            }
        }

        latch.await()

        assertThat(dataSourceExecCount.get()).isEqualTo(10)
    }
}