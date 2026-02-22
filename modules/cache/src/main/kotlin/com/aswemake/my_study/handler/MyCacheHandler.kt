package com.aswemake.my_study.handler

import com.aswemake.my_study.CacheStrategy
import java.time.Duration
import java.util.function.Supplier

interface MyCacheHandler {
    fun <T> fetch(key: String, ttl: Duration, dataSourceSupplier: Supplier<T>, clazz: Class<T>): T
    fun put(key: String, ttl: Duration, value: Any?)
    fun evict(key: String)
    fun supports(cacheStrategy: CacheStrategy): Boolean
}
