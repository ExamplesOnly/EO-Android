package com.solvevolve.examplesonly.network.auth;

import com.solvevolve.examplesonly.model.User;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AuthInterface {

    @FormUrlEncoded
    @POST("v1/auth/signup")
    public void createAccount(@Field("firstName") String firstName, @Field("lastName") String lastName,
                              @Field("email") String email, @Field("password") String password, @Field("gender") String gender,
                              @Field("dob") String dob, Callback<HashMap<String, String>> callback);

    @POST("v1/auth/signup")
    public Call<HashMap<String, String>> signUp(@Body User user);

    @POST("v1/auth/login")
    public Call<HashMap<String, String>> login(@Body User user);

    @FormUrlEncoded
    @POST("v1/auth/changePassword")
    public Call<HashMap<String, String>> changePassword(
            @Field("email") String email, @Field("password") String password,
            @Field("newPassword") String newPassword);
}
