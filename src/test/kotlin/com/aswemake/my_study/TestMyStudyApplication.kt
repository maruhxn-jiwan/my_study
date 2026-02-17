package com.aswemake.my_study

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<MyStudyApplication>().with(TestcontainersConfiguration::class).run(*args)
}
