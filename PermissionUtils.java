package com.rubenmimoun.beerchallenge;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtils {

    public static boolean checkLocationPermission(Context context){

        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED  && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ){
            return  true ;
        }
        return false ;

    }

    public static boolean checkFragmentLocationPermission(Context context){

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED  && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ){
            return  true ;
        }
        return false ;

    }
    public static void requestLocationPermission(Activity activity, int requestCode){
        ActivityCompat.requestPermissions(activity, new String []{Manifest.permission.ACCESS_FINE_LOCATION},requestCode);
        ActivityCompat.requestPermissions(activity, new String []{Manifest.permission.ACCESS_COARSE_LOCATION},requestCode);

    }





}
