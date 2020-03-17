package com.rubenmimoun.beerchallenge.Api;

import com.rubenmimoun.beerchallenge.Models.Places;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface Client {

    @GET("/users/{user}/repos")
    Call<List<Places>> reposForUser(
            @Path("user") String user
    );


}
