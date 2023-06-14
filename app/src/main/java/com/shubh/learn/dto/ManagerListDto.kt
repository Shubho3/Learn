package com.shubh.learn.dto

data class ManagerListDto(
    val message: String,
    val result: ArrayList<UserDto>,
    val status: String
):  java.io.Serializable