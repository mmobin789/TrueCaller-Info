package com.magicbio.truename.retrofit;


import com.magicbio.truename.models.GetNumberResponse;
import com.magicbio.truename.models.SignUpResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 27-Nov-17.
 */

public interface ApiInterface {


    @GET("index.php")
    Call<SignUpResponse> signup(@Query("action") String action, @Query("name") String name, @Query("email") String email
            , @Query("address") String address, @Query("city") String city, @Query("country") String country, @Query("mobile") String mobile);

    @GET("index.php")
    Call<GetNumberResponse> getNumber(@Query("action") String action, @Query("user_id") String user_id, @Query("cell") String cell);


}
