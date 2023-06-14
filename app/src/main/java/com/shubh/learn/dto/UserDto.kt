package com.shubh.learn.dto

import java.io.Serializable

data class UserDto(
    val address: String,
    val city: String,
    val country: String,
    val date_time: String,
    val dob: String,
    val email: String,
    val establishment_no: String,
    val expired_at: String,
    val first_name: String,
    val gender: String,
    val id: String,
    val image: String,
    val ios_register_id: String,
    val lang: String,
    val last_login: String,
    val last_name: String,
    val lat: String,
    val lon: String,
    val mobile: String,
    val object_name: String,
    val password: String,
    val register_id: String,
    val social_id: String,
    val status: String,
    val step: String,
    val token: String,
    val town: String,
    val type: String,
    val type_id: String,
    val user_name: String,
    val qr_status: String,
    val rewards_point: Int,
    val pr_qrcode_point: String,
    val pr_qrcode_status: String,
    val price_image: String

) : Serializable{
   /* override fun toString(): String {
        return "UserDto(address='$address', city='$city', country='$country', date_time='$date_time', dob='$dob', email='$email', establishment_no='$establishment_no', expired_at='$expired_at', first_name='$first_name', gender='$gender', id='$id', image='$image', ios_register_id='$ios_register_id', lang='$lang', last_login='$last_login', last_name='$last_name', lat='$lat', lon='$lon', mobile='$mobile', object_name='$object_name', password='$password', register_id='$register_id', social_id='$social_id', status='$status', step='$step', token='$token', town='$town', type='$type', type_id='$type_id', user_name='$user_name')"
    }*/


    override fun hashCode(): Int {
        var result = id.hashCode()
        if(id.isEmpty()){
            result = 31 * result + id.hashCode()
        }
        return result
    }
}