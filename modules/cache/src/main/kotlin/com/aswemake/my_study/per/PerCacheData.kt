package com.aswemake.my_study.per

import com.aswemake.my_study.DataSerializer
import java.time.Duration
import java.time.Instant
import java.util.random.RandomGenerator
import kotlin.math.ln

data class PerCacheData(
    val data: String,
    val computationTimeMillis: Long, // delta: 재계산에 소요된 시간
    val expiredAtMillis: Long, // expiry: 캐시가 만료되는 시점
) {
    fun <T> parseData(dataType: Class<T>): T? {
        return DataSerializer.deserializeOrNull(data, dataType)
    }

    /**
     * 캐시 갱신 책임이 있는지 확인
     */
    fun shouldRecompute(beta: Double): Boolean {
        val nowMillis = Instant.now().toEpochMilli()
        val rand = RandomGenerator.getDefault().nextDouble()
        return nowMillis - computationTimeMillis * beta * ln(rand) >= expiredAtMillis
    }

    companion object {
        fun of(data: Any?, computationTimeMillis: Long, ttl: Duration): PerCacheData {
            return PerCacheData(
                data = DataSerializer.serializeOrException(data),
                computationTimeMillis = computationTimeMillis,
                expiredAtMillis = Instant.now().plus(ttl).toEpochMilli()
            )
        }
    }
}
