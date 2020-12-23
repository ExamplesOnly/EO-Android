package com.solvevolve.examplesonly.network.category;

import com.solvevolve.examplesonly.model.Category;
import com.solvevolve.examplesonly.model.Video;
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
