package com.examplesonly.android.network;

import java.util.HashMap;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
interface AuthInterface {

    @FormUrlEncoded
    @POST("api/v1/auth/signup")
    public void createAccount(@Body String email, @Body String password, @Body String firstName,
            Callback<HashMap<String, String>> callback);
}
