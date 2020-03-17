package com.rubenmimoun.beerchallenge.Notification;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rubenmimoun.beerchallenge.MainActivity;
import com.rubenmimoun.beerchallenge.Models.BarList;
import com.rubenmimoun.beerchallenge.Models.Places;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class ParsingApi {

    private Context context ;
    // TODO : next lec

    private static ArrayList<Places> PLACES_ARRAY_LIST = new ArrayList<>();
    private ArrayList<Places>places ;
    private boolean loaded ;
     private BarList barList ;


    public ParsingApi(Context context) {
        this.context = context ;
    }

    public void ParsingLink() throws IOException {

        String response = "";
        InputStream inputStream = null;
        HttpURLConnection con = null;
        try {
            URL url = new URL(MainActivity.BAR_API);
            con = (HttpURLConnection) url.openConnection();
            con.connect();

            inputStream = con.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = ""; //ERROR
            while((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line);
            }

            response = stringBuffer.toString();
            inputStreamReader.close();
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(inputStream !=null){
                inputStream.close();
            }
            con.disconnect();
        }

        try {
            String bigSponse = response ;
            JSONObject object = new JSONObject(bigSponse);
            JSONArray resultsArray = object.getJSONArray("results");


            for (int i = 0; i < resultsArray.length(); i++) {

                JSONObject object1 = (JSONObject) resultsArray.get(i);
                String name = object1.getString("name");

                JSONObject geometryMain = (JSONObject) resultsArray.get(0);
                JSONObject geometry = geometryMain.getJSONObject("geometry");
                JSONObject location =  geometry.getJSONObject("location") ;
                String latitudeTemp = location.getString("lat");
                Double latitude = Double.parseDouble(latitudeTemp);
                String longitudeTemp =  location.getString("lng");
                Double longitude = Double.parseDouble(longitudeTemp);

                PLACES_ARRAY_LIST.add(new Places(name, latitude,longitude));
                barList = new BarList(PLACES_ARRAY_LIST, context);


            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public  void loadUrl(final VolleyCallBack callBack){


        RequestQueue queue = Volley.newRequestQueue(context);

// Request a string response from the provided URL.
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, MainActivity.BAR_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray resultsArray = object.getJSONArray("results");


                            for (int i = 0; i < resultsArray.length(); i++) {

                                JSONObject object1 = (JSONObject) resultsArray.get(i);
                                String name = object1.getString("name");

                                JSONObject geometryMain = (JSONObject) resultsArray.get(i);
                                JSONObject geometry = geometryMain.getJSONObject("geometry");
                                JSONObject location =  geometry.getJSONObject("location") ;
                                String latitudeTemp = location.getString("lat");
                                double latitude = Double.parseDouble(latitudeTemp);
                                String longitudeTemp =  location.getString("lng");
                                double longitude = Double.parseDouble(longitudeTemp);

                                PLACES_ARRAY_LIST.add(new Places(name, latitude,longitude));
                                barList = new BarList(PLACES_ARRAY_LIST, context);

                            }

                            callBack.onSuccess(PLACES_ARRAY_LIST);



                        } catch (Exception e) {
                            e.printStackTrace();
                        }



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


// Add the request to the Request
        queue.add(stringRequest);


    }



}
