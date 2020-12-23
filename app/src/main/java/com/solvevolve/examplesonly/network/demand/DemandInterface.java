package com.solvevolve.examplesonly.network.demand;

import com.solvevolve.examplesonly.model.Demand;
import com.solvevolve.examplesonly.model.Video;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DemandInterface {

    @POST("v1/demand/add")
    public Call<Demand> addDemand(@Body Demand demand);

    @GET("v1/demand/list")
    public Call<ArrayList<Demand>> getDemands();

    @GET("v1/demand/{demandId}/videos")
    public Call<ArrayList<Video>> getDemandVideos(@Path("demandId") String demandId);

    @POST("v1/demand/bookmark")
    public Call<ArrayList<Video>> bookmarkDemand(@Field("username") String demandId);
}
