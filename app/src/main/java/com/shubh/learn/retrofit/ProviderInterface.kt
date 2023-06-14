package com.shubh.learn.retrofit


import com.shubh.learn.dto.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ProviderInterface {
    @FormUrlEncoded
    @POST("add_error_msg")
    fun add_error_msg(@FieldMap params: Map<String, String>): Call<ResponseBody>
    @FormUrlEncoded
    @POST("login")
    fun login(@FieldMap params: Map<String, String>): Call<LoginDto>

    @FormUrlEncoded
    @POST("social_login")
    fun social_login(@FieldMap params: Map<String, String>): Call<LoginDto>

    @FormUrlEncoded
    @POST("signup")
    fun signup(@FieldMap params: Map<String, String>): Call<LoginDto>

    @FormUrlEncoded
    @POST("add_update_lang")
    fun add_update_lang(@FieldMap params: Map<String, String>): Call<ResponseBody>

    //user_id:
    //token:
    // lang:  English','Polish')
    @FormUrlEncoded
    @POST("add_manager")
    fun add_manager(@FieldMap params: Map<String, String>): Call<LoginDto>

    @FormUrlEncoded
    @POST("add_company")
    fun add_company(@FieldMap params: Map<String, String>): Call<LoginDto>


    @GET("get_managerList")
    fun get_managerList(@QueryMap params: Map<String, String>): Call<ManagerListDto>

    @GET("get_companyList")
    fun get_companyList(@QueryMap params: Map<String, String>): Call<ManagerListDto>

    @Multipart
    @POST("add_qr_image")
    fun add_qr_image(
        @Part("user_id") user_id: RequestBody,
        @Part("token") token: RequestBody,
        @Part("qr_code") qr_code: RequestBody,
        @Part("company_id") comp: RequestBody,
        @Part file: MultipartBody.Part
    ):
            Call<ResponseBody>

    @Multipart
    @POST("add_gallery")
    fun add_gallery(
        @Part("friend_id") friend_id: RequestBody,
        @Part("user_id") user_id: RequestBody,
        @Part("token") token: RequestBody,
        @Part file: MultipartBody.Part
    ):
            Call<ResponseBody>

    @FormUrlEncoded
    @POST("add_rewards")
    fun add_rewards(@FieldMap params: Map<String, String>): Call<LoginDto>

    @FormUrlEncoded
    @POST("get_rewards")
    fun get_rewards(@FieldMap params: Map<String, String>): Call<PointsDto>

    @FormUrlEncoded
    @POST("get_profile")
    fun get_profile(@FieldMap params: Map<String, String>): Call<LoginDto>

    @GET("get_point")
    fun get_point(@QueryMap params: Map<String, String>): Call<AdminPointsDto>

    @FormUrlEncoded
    @POST("update_rewards")
    fun update_rewards(@FieldMap params: Map<String, String>): Call<AdminPointsDto>
 @FormUrlEncoded
    @POST("delete_user")
    fun delete_user(@FieldMap params: Map<String, String>): Call<ResponseData>

    @FormUrlEncoded
    @POST("add_notes")
    fun add_notes(@FieldMap params: Map<String, String>): Call<ResponseBody>

    @FormUrlEncoded
    @POST("delete_notes")
    fun delete_notes(@FieldMap params: Map<String, String>): Call<ResponseBody>
   @FormUrlEncoded
    @POST("update_wallet_point")
    fun update_wallet_point(@FieldMap params: Map<String, String>): Call<LoginDto>
   @FormUrlEncoded
    @POST("update_per_qrcode")
    fun update_per_qrcode(@FieldMap params: Map<String, String>): Call<LoginDto>

   // @GET("get_gallery")
    @GET("get_all_gallery")
    fun get_gallery(@QueryMap params: Map<String, String>): Call<PicturesListDto>
 @GET("get_all_gallery")
    fun get_all_gallery(@QueryMap params: Map<String, String>): Call<PicturesListDto>

    @GET("get_notes")
    fun get_notes(@QueryMap params: Map<String, String>): Call<NotesDto>
}