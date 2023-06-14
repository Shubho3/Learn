package com.shubh.learn.dto
data class PicturesListDto(
    val message: String,
    val result: ArrayList<Result>,
    val status: String
):java.io.Serializable
{
data class Result(
    val date_time: String,
    val id: String,
    val image: String,
    val user_id: String
):java.io.Serializable
}