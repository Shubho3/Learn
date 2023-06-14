package com.shubh.learn.dto

import java.io.Serializable



data class LoginDto(
    val message: String,
    val result: UserDto,
    val status: String
):  Serializable