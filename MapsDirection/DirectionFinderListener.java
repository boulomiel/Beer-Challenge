package com.rubenmimoun.beerchallenge.MapsDirection;

import com.rubenmimoun.beerchallenge.Models.Route;

import java.util.List;

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
