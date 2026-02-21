package com.aswemake.my_study.domain.command

data class UserCreateCommand(
    val name: String,
    val email: String,
)