package com.magicbio.truename.retrofit;


import com.magicbio.truename.db.contacts.Contact;
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

    @GET("signup")
    Call<SignUpResponse> signup(@Query("number") String number, @Query("country_id") String countryId);

    @GET("number-detail")
    Call<GetNumberResponse> getNumberDetails(@Query("number") String number, @Query("code") String countryId);


}
