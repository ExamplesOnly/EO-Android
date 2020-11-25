package com.examplesonly.android.network.video;

import com.examplesonly.android.model.Video;
import java.util.ArrayList;
import java.util.HashMap;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
public interface VideoInterface {

    @Multipart
    @POST("v1/video/upload")
    Call<HashMap<String, String>> upload(
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("categories") RequestBody categories,
            @Part("duration") RequestBody duration,
            @Part("height") RequestBody height,
            @Part("width") RequestBody width,
            @Part("demandId") RequestBody demandId,
            @Part MultipartBody.Part file,
            @Part MultipartBody.Part thumbnail
    );

    @GET("/v1/video/list")
    Call<ArrayList<Video>> getVideos();
}
