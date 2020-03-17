package com.rubenmimoun.beerchallenge.Models;

import android.content.Context;

import com.rubenmimoun.beerchallenge.Notification.ParsingApi;

import java.util.ArrayList;


public class BarList {

    private ArrayList<Places> barList;
    private Context context ;
    public BarList(ArrayList<Places> chosenBars, Context context){
        this.barList = chosenBars ;
        this.context =context ;

    }


    public void add(Places places){
        barList.add(places);
    }


    public ArrayList<Places> getBarList() {
        return barList;
    }

    public void setBarList(ArrayList<Places> barList) {
        this.barList = barList;
    }

    public int size(){
        return barList.size();
    }

    public boolean contains(Places place){
        if( barList.contains(place)){
            return  true ;
        }
        return false ;
    }

    public Places getBar(int i){
      return  barList.get(i) ;
    }

    public ArrayList<Places> getAll(){
        return  barList ;
    }

    public String toString(){

        String result = "" ;

        for ( Places place: barList){
            result +=  "name:" + place.getName()+ "  latitude :" + place.getLat() +" longitude:"+ place.getLng() +"\n";
        }

        return result ;
    }

}
