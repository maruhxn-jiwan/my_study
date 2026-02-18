package com.aswemake.my_study.domain

data class UserCreateCommand(
    val name: String,
    val email: String,
)
