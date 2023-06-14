package com.shubh.learn.dto

class NotificationData (
    val key: String,
    val point: Int,
    val result: String,
    val title: String
):java.io.Serializable{
    override fun toString(): String {
        return "NotificationData(key='$key', point=$point, result='$result', title='$title')"
    }
}

