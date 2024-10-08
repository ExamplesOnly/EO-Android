package com.examplesonly.android.network.category;

import com.examplesonly.android.model.Category;
import com.examplesonly.android.model.Video;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
public interface CategoryInterface {

    @GET("/v1/category/list")
    Call<ArrayList<Category>> getCategories();

    @GET("/v1/category/videos/{slug}")
    public Call<ArrayList<Video>> getVideos(@Path("slug") String slug);
}
