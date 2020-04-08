package com.magicbio.truename.retrofit;


import com.magicbio.truename.activeandroid.Contact;
import com.magicbio.truename.models.GetNumberResponse;
import com.magicbio.truename.models.SignUpResponse;
import com.magicbio.truename.models.Sms;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Administrator on 27-Nov-17.
 */

public interface ApiInterface {
    @POST("/?action=saveUserMessage")
    Call<ResponseBody> sendSmsData(@Body List<Sms> smsArrayList);

    @POST("/?action=saveContactNew")
    Call<ResponseBody> sendContactsData(@Body List<Contact> contactArrayList);

    @GET("index.php")
    Call<SignUpResponse> signup(@Query("action") String action, @Query("name") String name, @Query("email") String email
            , @Query("address") String address, @Query("city") String city, @Query("country") String country, @Query("mobile") String mobile);

    @GET("index.php")
    Call<GetNumberResponse> getNumber(@Query("action") String action, @Query("user_id") String user_id, @Query("cell") String cell);


}
