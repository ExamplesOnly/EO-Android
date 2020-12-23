package com.solvevolve.examplesonly.network.user;

import com.solvevolve.examplesonly.model.Category;
import com.solvevolve.examplesonly.model.Demand;
import com.solvevolve.examplesonly.model.User;
import com.solvevolve.examplesonly.model.Video;
import java.util.ArrayList;
import java.util.HashMap;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserInterface {

    @GET("v1/user/me")
    Call<User> me();

    @GET("v1/user/videos")
    Call<ArrayList<Video>> myVideos();

    @FormUrlEncoded
    @POST("v1/user/addInterests")
    Call<HashMap<String, String>> updateInterest(@Field("categories") String categories);

    @GET("v1/user/interests")
    Call<ArrayList<Category>> getInterest();

    @GET("v1/auth/verify/{token}")
    Call<HashMap<String, String>> verifyAccount(@Path("token") String token);

    @POST("v1/user/update/profile")
    Call<HashMap<String, String>> updateUser(@Body User user);

    @Multipart
    @POST("v1/user/update/profileImage")
    Call<HashMap<String, String>> updateProfileImage(@Part MultipartBody.Part file);

    @Multipart
    @POST("v1/user/update/coverImage")
    Call<HashMap<String, String>> updateCoverImage(@Part MultipartBody.Part file);

    @GET("v1/user/myDemands")
    Call<ArrayList<Demand>> getDemands();

    @GET("v1/user/getProfile/{uuid}")
    Call<User> getUserProfile(@Path("uuid") String uuid);

}
