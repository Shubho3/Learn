package com.shubh.learn.dto

data class PointsDto(
    val message: String,
    val result: ArrayList<Result>,
    val status: String
):java.io.Serializable {
    data class Result(
        val date_time: String,
        val id: String,
        val last_name: String,
        val other_first_name: String,
        val other_id: String,
        val other_last_name: String,
        val point: String,
        val user_id: String,
        val user_name: String
    ):java.io.Serializable
}