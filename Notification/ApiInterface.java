package com.rubenmimoun.beerchallenge.Notification;

import com.rubenmimoun.beerchallenge.Models.Places;
import com.rubenmimoun.beerchallenge.Models.PlacesList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

   // @GET("/api/place")
 //   Call<MultipleResource> doGetListResources();

    @POST("/api/place")
    Call<Places> createPlace(@Body Places places);

    @GET("/api/users?")
    Call<PlacesList> doGetUserList(@Query("page") String page);

    @FormUrlEncoded
    @POST("/api/users?")
    Call<PlacesList> doCreateUserWithField(@Field("name") String name, @Field("job") String job);
}


