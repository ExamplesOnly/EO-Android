package com.examplesonly.android.network.user;

import com.examplesonly.android.model.User;
import com.examplesonly.android.model.Video;
import java.util.ArrayList;
import java.util.HashMap;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UserInterface {

    @GET("v1/user/me")
    public Call<User> me();

    @GET("v1/user/videos")
    public Call<ArrayList<Video>> myVideos();

    @GET("v1/auth/verify/{token}")
    public Call<HashMap<String, String>> verifyAccount(@Path("token") String token);
}
