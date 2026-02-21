package com.aswemake.my_study.annotation

import com.aswemake.my_study.CacheStrategy

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class MyCacheEvict(
    val strategy: CacheStrategy,
    val cacheName: String,
    val key: String,
)
