package com.pavithran.pricely;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface PredictionApiService {

    @GET("/")
    Call<Object> getRootStatus();

    @GET("health")
    Call<Object> getHealthStatus();

    @POST("predict")
    Call<PredictionResponse> predictPrice(@Body PredictionRequest request);
}
