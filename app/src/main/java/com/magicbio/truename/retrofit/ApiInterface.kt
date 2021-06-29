package com.magicbio.truename.retrofit

import com.magicbio.truename.models.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {
    @GET("signup")
    fun signup(
        @Query("number") number: String?,
        @Query("country_id") countryId: String?
    ): Call<SignUpResponse?>?

    @GET("number-detail")
    fun getNumberDetails(
        @Query("number") number: String,
        @Query("code") countryId: String
    ): Call<GetNumberResponse?>

    @POST("upload-numbers")
    fun uploadContacts(@Body uploadContactsRequest: UploadContactsRequest): Call<UploadContactsResponse>

    @POST("upload-numbers")
    fun uploadContactsSync(@Body uploadContactsRequest: UploadContactsRequest): Call<UploadContactsResponse>

    @GET("get-msg")
    suspend fun invite(@Query("type") type: String): InviteResponse

    @GET("get-msg")
    fun inviteSync(@Query("type") type: String): Call<InviteResponse>

    @GET("app-update-status")
    suspend fun checkAppUpdate(@Query("user_id") userId: Int): AppUpdateResponse

    @GET("update-app-status?status=no")
    suspend fun notifyAppUpToDate(@Query("user_id") userId: Int): AppUpdateResponse

}