package com.magicbio.truename.retrofit

import com.magicbio.truename.models.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {
    @POST("/?action=saveUserMessage")
    fun sendSmsData(@Body smsArrayList: ArrayList<Sms>): Call<ResponseBody>

    @GET("signup")
    fun signup(
        @Query("number") number: String?,
        @Query("country_id") countryId: String?
    ): Call<SignUpResponse?>?

    @GET("number-detail")
    fun getNumberDetails(
        @Query("number") number: String?,
        @Query("code") countryId: String?
    ): Call<GetNumberResponse?>?

    @POST("upload-numbers")
    suspend fun uploadContacts(@Body uploadContactsRequest: UploadContactsRequest): UploadContactsResponse

    @POST("upload-numbers")
    fun uploadContactsSync(@Body uploadContactsRequest: UploadContactsRequest): Call<UploadContactsResponse>

    @GET("get-msg")
    suspend fun invite(@Query("type") type: String): InviteResponse
}