package com.examplesonly.android.network.category;

import com.examplesonly.android.model.Category;
import com.examplesonly.android.model.Video;
import java.util.ArrayList;
import java.util.HashMap;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
public interface CategoryInterface {

    @GET("/v1/category/list")
    Call<ArrayList<Category>> getCategories();

    @FormUrlEncoded
    @POST("/j")
    Call<HashMap<String, String>> updateInterests(@Field("categories") String categories);



    @GET("/v1/category/videos/{slug}")
    public Call<ArrayList<Video>> getVideos(@Path("slug") String slug);
}
