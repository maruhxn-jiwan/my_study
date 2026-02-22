package com.aswemake.my_study.bloom_filter

fun interface BloomFilterHashFunction {
    fun hash(value: String): Long
}