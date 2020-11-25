package com.examplesonly.android.network.user;

import com.examplesonly.android.model.Category;
import com.examplesonly.android.model.Demand;
import com.examplesonly.android.model.User;
import com.examplesonly.android.model.Video;
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
    public Call<User> me();

    @GET("v1/user/videos")
    public Call<ArrayList<Video>> myVideos();

    @FormUrlEncoded
    @POST("v1/user/addInterests")
    public Call<HashMap<String, String>> updateInterest(@Field("categories") String categories);

    @GET("v1/user/interests")
    public Call<ArrayList<Category>> getInterest();

    @GET("v1/auth/verify/{token}")
    public Call<HashMap<String, String>> verifyAccount(@Path("token") String token);

    @POST("v1/user/update/profile")
    public Call<HashMap<String, String>> updateUser(@Body User user);

    @Multipart
    @POST("v1/user/update/profileImage")
    public Call<HashMap<String, String>> updateProfileImage(@Part MultipartBody.Part file);

    @Multipart
    @POST("v1/user/update/coverImage")
    public Call<HashMap<String, String>> updateCoverImage(@Part MultipartBody.Part file);

    @GET("v1/user/myDemands")
    public Call<ArrayList<Demand>> getDemands();

}
