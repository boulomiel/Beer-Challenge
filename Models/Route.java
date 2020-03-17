package com.rubenmimoun.beerchallenge.Models;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Route {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public List<LatLng> points;

    @Override
    public String toString() {
        return "Route{" +
                "distance=" + distance +
                ", duration=" + duration +
                ", endAddress='" + endAddress + '\'' +
                ", endLocation=" + endLocation +
                ", startAddress='" + startAddress + '\'' +
                ", startLocation=" + startLocation +
                ", points=" + points +
                '}';
    }
}