package com.rubenmimoun.beerchallenge.Adapter;
import com.rubenmimoun.beerchallenge.Notification.MyResponse;
import com.rubenmimoun.beerchallenge.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIServices {

    @Headers(

            {

                    "Content-Type:application/json",
                    "Authorization:key=AAAAgMF5cSY:APA91bGE3hCeIih7fDE1eqO2lp2qY_dMGoOJRPDw1vmoCko_CLLzZud5QmLuBwVIMR24m4vxugI7ADvXjzEFSIH7ROs0-gxAQiz1FzmsBB38ATy4neV9DCpSah1ITU3d7JdZUSdEMXrt"

            }

    )
    @POST("fcm/send")
    Call<MyResponse>sendNotification(@Body Sender body);

}
