package com.rubenmimoun.beerchallenge.Models;

import java.util.ArrayList;

public class PlacesList {

    ArrayList<Places>list ;

    public PlacesList( ArrayList<Places> list){
        this.list = list ;
    }



    public void addToList(Places places){
        list.add(places);
    }


    public Places getPlaces(int i){
        return  list.get(i);
    }


    public Places getPlaces(Places places){
        for ( Places place: list) {
            if( place.getName().equalsIgnoreCase(places.getName())){
                return place ;
            }
        }
        return  null ;
    }


}
