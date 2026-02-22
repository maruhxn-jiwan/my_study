package com.aswemake.my_study.bloom_filter

import com.google.common.hash.Hashing
import java.nio.charset.StandardCharsets
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.pow

/**
 * 데이터는 k개의 독립적인 해시함수에 의해 Bloom Filter에 입력됩니다
 * - 각 해시값은 비트의 인덱스 번호가 됩니다
 * - BloomFilter의 k개의 인덱스의 비트를 1로 변경하여 데이터를 입력합니다
 *
 * 데이터 삽입 시, 해당 데이터의 k개의 해시값을 통해 인덱스가 1인지 확인
 * - 하나라도 0이라면 -> 데이터 삽입된 적 없음
 * - 모두 1이라면 -> 있을 수도 있다고 판단
 *
 * 한 번 입력된 데이터를 삭제할 수는 없습니다 (BloomFilter는 어떤 value에 의해 1로 입력되었는지 모르기 때문)
 */
data class BloomFilter(
    val id: String,
    val dataCount: Long, // 데이터 수(n)
    val falsePositiveRate: Double, // 오차율(p)
    val bitSize: Long, // 비트 수(m)
    val hashFunctionCount: Int, // 해시 함수 수(k)
    val hashFunctions: List<BloomFilterHashFunction>, // 해시 함수 리스트
) {

    /**
     * value에 대한 해시값 리스트를 반환합니다
     */
    fun hash(value: String): List<Long> = hashFunctions.map { it.hash(value) }

    companion object {
        fun create(
            id: String,
            dataCount: Long,
            falsePositiveRate: Double,
        ): BloomFilter {
            if (dataCount <= 0) {
                throw IllegalArgumentException("데이터 수는 0 이하일 수 없습니다.")
            }

            if (falsePositiveRate <= 0.0 || falsePositiveRate >= 1.0) {
                throw IllegalArgumentException("오차율은 0 ~ 1 범위 내의 값이어야 합니다.")
            }

            val bitSize = calculateBitSize(dataCount, falsePositiveRate)
            val hashFunctionCount = calculateHashFunctionCount(dataCount, bitSize)

            val hashFunctions = (0 until hashFunctionCount).map { seed ->
                BloomFilterHashFunction { value ->
                    abs(
                        Hashing.murmur3_128(seed)
                            .hashString(value, StandardCharsets.UTF_8)
                            .asLong() % bitSize
                    )
                }
            }

            return BloomFilter(
                id = id,
                dataCount = dataCount,
                falsePositiveRate = falsePositiveRate,
                bitSize = bitSize,
                hashFunctionCount = hashFunctionCount,
                hashFunctions = hashFunctions
            )
        }

        /**
         * -(n * ln(p)) / (ln(2))^2
         * @param dataCount -> n
         * @param falsePositiveRate -> p
         */
        private fun calculateBitSize(dataCount: Long, falsePositiveRate: Double) =
            ceil(-(dataCount * ln(falsePositiveRate) / ln(2.0).pow(2))).toLong()

        /**
         * (m / n) * ln(2)
         * @param dataCount -> n
         * @param bitSize -> m
         */
        private fun calculateHashFunctionCount(dataCount: Long, bitSize: Long): Int {
            return ceil(bitSize / dataCount.toDouble() * ln(2.0)).toInt()
        }
    }
}