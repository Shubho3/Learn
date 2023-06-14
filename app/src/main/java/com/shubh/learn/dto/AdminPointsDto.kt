package com.shubh.learn.dto

data class AdminPointsDto(
    val message: String,
    val result: Result,
    val status: String
)

data class Result(
    val date_time: String,
    val id: String,
    val point: String
)