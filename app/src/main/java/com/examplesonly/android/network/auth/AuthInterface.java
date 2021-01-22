package com.examplesonly.android.network.auth;

import com.examplesonly.android.model.User;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthInterface {

    @POST("v1/auth/sessionsignup")
    Call<HashMap<String, String>> signUp(@Body User user);

    @POST("v1/auth/sessionlogin")
    Call<HashMap<String, String>> login(@Body User user);

    @FormUrlEncoded
    @POST("v1/auth/socialsignin/google")
    Call<HashMap<String, String>> googleLogin(@Field("idToken") String idToken, @Field("authCode") String authCode);

    @FormUrlEncoded
    @POST("v1/auth/changePassword")
    Call<HashMap<String, String>> changePassword(
            @Field("email") String email, @Field("password") String password,
            @Field("newPassword") String newPassword);

    @FormUrlEncoded
    @POST("v1/auth/forgotPassword")
    Call<HashMap<String, String>> forgotPassword(
            @Field("email") String email);

    @FormUrlEncoded
    @POST("v1/auth/resetPassword")
    Call<HashMap<String, String>> resetPassword(
            @Field("password") String password,
            @Field("token") String token);

    @FormUrlEncoded
    @POST("v1/auth/refreshToken")
    Call<HashMap<String, String>> refreshToken(
            @Field("refreshToken") String refreshToken);

    @FormUrlEncoded
    @POST("v1/auth/logout")
    Call<HashMap<String, String>> logout(
            @Field("refreshToken") String refreshToken);
}
