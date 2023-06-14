package com.shubh.learn.dto

import java.io.Serializable

data class NotesDto(
    val message: String,
    val result: ArrayList<Result>,
    val status: String
):Serializable {

    data class Result(
        val date_time: String,
        val id: String,
        val image: String,
        val notes: String,
        val topic: String,
        val first_name: String,
        val last_name: String,
        val time_ago: String,
        val user_id: String
    ) : Serializable
}