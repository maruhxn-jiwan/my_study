package com.aswemake.my_study.handler

import com.aswemake.my_study.CacheStrategy
import com.aswemake.my_study.DataSerializer
import com.aswemake.my_study.distributed_lock.DistributedLockProvider
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.function.Supplier

/**
 * 여기서는 Redis Global Lock을 활용했지만, Local Lock을 활용해도 됨 (관리 포인트 줄이기)
 * 물론, DataSource로 전파되는 요청이 애플리케이션 수만큼 늘어나겠지만 문제될 수준은 X
 *
 * 분산락 해제 감지를 위해 Polling을 사용 중인데, Pub/Sub 방식도 방법임 (물론 구현 난이도는 증가)
 */
@Component
class RequestCollapsingCacheHandler(
    private val redisTemplate: StringRedisTemplate,
    private val distributedLockProvider: DistributedLockProvider
) : MyCacheHandler {

    companion object {
        private val POLLING_INTERVAL_MILLIIS = 50L // 락 획득 재시도 간격
        private val REFRESH_WAITING_TIMEOUNT_MILLIS = 2000L // 요청별 타임아웃 시간
    }

    override fun <T> fetch(
        key: String,
        ttl: Duration,
        dataSourceSupplier: Supplier<T?>,
        clazz: Class<T>
    ): T? {
        var cached = redisTemplate.opsForValue().get(key)
            ?.let { return DataSerializer.deserializeOrNull(it, clazz)!! }

        val lockKey = genLockKey(key)
        // 락 획득에 성공하면 캐시 갱신 후, 점유 해제
        val lockTtl = Duration.ofSeconds(3)
        if (distributedLockProvider.lock(lockKey, lockTtl)) {
            try {
                return refresh(key, ttl, dataSourceSupplier)
            } finally {
                distributedLockProvider.unlock(lockKey)
            }
        }

        // 락 획득에 실패한 경우, 2초(요청별 타임아웃)동안 다음 동작 반복 실행
        val start = System.nanoTime()
        while (System.nanoTime() - start < TimeUnit.MILLISECONDS.toNanos(REFRESH_WAITING_TIMEOUNT_MILLIS)) {
            // 캐시 조회해보고 있으면 반환, 없으면 sleep으로 기다림 => Polling
            cached = redisTemplate.opsForValue().get(key)
                ?.let { return DataSerializer.deserializeOrNull(it, clazz)!! }

            try {
                /**
                 * sleep 시점이면 갱신 여부 확인을 못하므로 요청별 타임아웃 시간을 둠
                 * => 특정 상황에 특정 요청이 무한 대기 가능성을 막기 위해 요청별 타임아웃을 둠 (최소한의 종료 정책)
                 */
                TimeUnit.MILLISECONDS.sleep(POLLING_INTERVAL_MILLIIS)
            } catch (e: InterruptedException) {
                break
            }
        }

        return refresh(key, ttl, dataSourceSupplier)
    }

    private fun <T> refresh(key: String, ttl: Duration, dataSourceSupplier: Supplier<T?>): T? {
        val sourceResult = dataSourceSupplier.get()
        put(key, ttl, sourceResult)
        return sourceResult
    }

    private fun genLockKey(key: String) = "${CacheStrategy.REQUEST_COLLAPSING}:lock:$key"

    override fun put(key: String, ttl: Duration, value: Any?) {
        redisTemplate.opsForValue().set(key, DataSerializer.serializeOrException(value), ttl)
    }

    override fun evict(key: String) {
        redisTemplate.delete(key)
    }

    override fun supports(cacheStrategy: CacheStrategy) = CacheStrategy.REQUEST_COLLAPSING == cacheStrategy
}