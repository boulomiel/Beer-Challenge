package com.rubenmimoun.beerchallenge.FragmentAcvities;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rubenmimoun.beerchallenge.ChallengeActivities.ActivityChallengeOne;
import com.rubenmimoun.beerchallenge.ChallengeActivities.EndGame;
import com.rubenmimoun.beerchallenge.MainActivity;
import com.rubenmimoun.beerchallenge.MapsDirection.BringURL;
import com.rubenmimoun.beerchallenge.MapsDirection.DirectionFinderListener;
import com.rubenmimoun.beerchallenge.Models.Places;
import com.rubenmimoun.beerchallenge.Models.Route;
import com.rubenmimoun.beerchallenge.Models.User;
import com.rubenmimoun.beerchallenge.Notification.ParsingApi;
import com.rubenmimoun.beerchallenge.Notification.VolleyCallBack;
import com.rubenmimoun.beerchallenge.PermissionUtils;
import com.rubenmimoun.beerchallenge.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Double.parseDouble;

/**
 * A simple {@link Fragment} subclass.
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class MAPFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, DirectionFinderListener, LocationListener {


    private FirebaseUser firebaseUser;
    private DatabaseReference fromUserRef;
    private DatabaseReference otherUserRef;

    public static final int MY_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap gMap;
    private FusedLocationProviderClient apiClient;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationManager locationManager;
    private LatLng userLatlng;

    private ArrayList<Places> barArrayList;
    private ArrayList<Places> selectedBars = new ArrayList<>();
    private List<Polyline> polyLinePaths = new ArrayList<>();
    private Location mCurrentlocation;

    private Marker enemyMarker ;
    private boolean permissionGranted  ;

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient == null) {
            return;
        }
        mGoogleApiClient.connect();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
       locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }


    @Override
    public void onStop() {
        super.onStop();
        if(mGoogleApiClient != null){
            mGoogleApiClient.disconnect();
        }
        apiClient.removeLocationUpdates(locationCallback);



    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onResume() {
        super.onResume();
        if (permissionGranted) {
            if (mGoogleApiClient.isConnected()) {
                requestLocationUpdate();
            }
        }


    }

    public MAPFragment() {
        // Required empty public constructor
    }


    @Override
    public void onPause() {
        super.onPause();
        apiClient.removeLocationUpdates(locationCallback);

    }




    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_map, container, false);

        apiClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        fromUserRef = FirebaseDatabase.getInstance().getReference(MainActivity.USERS);
        otherUserRef = FirebaseDatabase.getInstance().getReference(MainActivity.USERS).child(ActivityChallengeOne.OTHERUSER);
        ArrayList<Marker> enemyMarkerList = new ArrayList<>();



        final SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment));  //use SuppoprtMapFragment for using in fragment instead of activity  MAPFragment = activity   SupportMapFragment = fragment
        assert mapFragment != null;
        mapFragment.getMapAsync(this);


        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSION_REQUEST_CODE);

        barArrayList  =  new ArrayList<>();

        requestLocationUpdate();
        createLocationRequest();
        startLocationUpdates();



        return v;
    }

    private void mapSetup(final GoogleMap googleMap){


            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.clear();
            setUserLocationMarker(googleMap);



            Timer timer =  new Timer();
            TimerTask task = new TimerTask() {


                @Override
                public void run() {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setLocationMarker(googleMap,pickThreeBars(barArrayList));

                            fromUserRef.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    User user = dataSnapshot.getValue(User.class) ;
                                    assert user != null;
                                    if( user.getId().equals(firebaseUser.getUid())){
                                        double latitude  = parseDouble(user.getLatitude());
                                        double longitude =  parseDouble(user.getLongitude());
                                        setPathToDestination(googleMap,new LatLng(latitude, longitude));
                                    }

                                }

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                }

                                @Override
                                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                }

                                @Override
                                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            setChallengerMarkerLocaTion(googleMap);
                        }
                    });


                }
            };
            //Toast.makeText(getContext(), "Choose three of them and run !", Toast.LENGTH_SHORT).show();


            timer.schedule(task,10*1000);

            compareChallengerPosition(selectedBars);




    }


    private static int getRandom(int max){
        return  (int)(Math.random()*(max+1));

    }

    private void startLocationUpdates() {
        apiClient.requestLocationUpdates(mLocationRequest,
                locationCallback,
                Looper.getMainLooper());
    }


    private ArrayList<Places>  pickThreeBars(ArrayList<Places> bars){

        selectedBars.clear();
        while (selectedBars.size() <2){
            int random = getRandom(bars.size()-1);
            for (int i = 0; i <bars.size() ; i++) {
             if( i == random ){
                 if(  !selectedBars.contains(bars.get(i))){
                     selectedBars.add(bars.get(random));

                 }

             }

            }
        }
        System.out.println("SELECTED"  +selectedBars.size());
        return  selectedBars ;
    }



    private void compareChallengerPosition(final ArrayList<Places>selectedBars){

        int count = 2;


                for (int i = 0; i <selectedBars.size() ; i++) {

                    if(mCurrentlocation == null)return;

                    if(mCurrentlocation.getLatitude() == selectedBars.get(i).getLat()
                              && mCurrentlocation.getLongitude() == selectedBars.get(i).getLng()){
                        count-- ;

                        Toast.makeText(getContext(), "Remains you" + count  +"bars to visit !", Toast.LENGTH_SHORT).show();

                        final int finalCount = count;

                                if( finalCount == 0){
                                    Intent intent = new Intent(getContext(), EndGame.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK) ;
                                    startActivity(intent);
                                }




                    }

                }


    }


    private String getUrl(LatLng origin, LatLng dest, String mode){

        String str_origin = "origin=" +origin.latitude + "," +origin.longitude;
        String str_dest  = "destination=" +dest.latitude + "," +dest.longitude;
        String str_mode =  "mode=" + mode ;
        String parameters =  str_origin + "&" + str_dest + "&" + str_mode;
        String output = "json" ;

        return "https://maps.googleapis.com/maps/api/directions/" +
                output+ "?"
                + parameters
                + "&key="
                + getString(R.string.google_api_key);
    }


    private void setPathToDestination(final GoogleMap gMap,LatLng userLoc){

        new BringURL(getContext(), gMap, this)
                .execute(getUrl(userLoc,
                        new LatLng(selectedBars.get(0).getLat(), selectedBars.get(0).getLng()),
                        "walking")
                        ,"walking");


        for (int i = 0; i <selectedBars.size()-1 ; i++) {


                new BringURL(getContext(), gMap, this)
                        .execute(getUrl(new LatLng(selectedBars.get(i).getLat(), selectedBars.get(i).getLng()),
                                new LatLng(selectedBars.get(i+1).getLat(), selectedBars.get(i+1).getLng()),
                                "walking")
                                ,"walking");


        }

    }


    private void setLocationMarker(GoogleMap gMap ,ArrayList<Places>locationMarkerList){

        for ( int i = 0 ; i< locationMarkerList.size() ; i ++){

            if( i <= 2){
                gMap.addMarker(new MarkerOptions()
                        .position(new LatLng(locationMarkerList.get(i).getLat(),locationMarkerList.get(i).getLng()))
                        .title(locationMarkerList.get(i).getName())
                        .icon(bitmapDescriptorFromVector(getContext(),R.drawable.beer)));
            }

        }
    }


    private void setUserLocationMarker(final GoogleMap gMap){
        if(!PermissionUtils.checkLocationPermission(getContext())){
            PermissionUtils.requestLocationPermission(getActivity(),MY_PERMISSION_REQUEST_CODE);
            return;
        }

        assert locationManager != null;
        final Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        assert location != null;
        fromUserRef.child(firebaseUser.getUid()).child("latitude").setValue(String.valueOf(location.getLatitude()));
        fromUserRef.child(firebaseUser.getUid()).child("longitude").setValue(String.valueOf(location.getLongitude()));
        fromUserRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user =  dataSnapshot.getValue(User.class) ;
                assert user != null;
                if(user.getId().equalsIgnoreCase(firebaseUser.getUid())){

                    userLatlng = new LatLng(location.getLatitude(),location.getLongitude());
                    gMap.addMarker(new MarkerOptions()
                            .position(userLatlng)
                            .icon(bitmapDescriptorFromVector(getContext(),R.drawable.crowns))
                            .title("Your position")
                    );


                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatlng, 16));
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatlng, 15));
                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setChallengerMarkerLocaTion(final GoogleMap gMap){



        fromUserRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class) ;
                assert user != null;
                if(user.getId().equalsIgnoreCase(ActivityChallengeOne.OTHERUSER)){


                        double latitude, longitude ;
                        latitude =  parseDouble(user.getLatitude());
                        longitude = parseDouble(user.getLongitude());




                        LatLng challengerLatLng = new LatLng(latitude,longitude);

                    enemyMarker = gMap.addMarker(new MarkerOptions()
                                .position(challengerLatLng)
                                .icon(bitmapDescriptorFromVector(getContext(),R.drawable.ogre))
                                .title("Challenger's position"));


                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }


            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void updateFirebaseLocation(){

        final double latitude = mCurrentlocation.getLatitude();
        final double longitude =  mCurrentlocation.getLongitude() ;

        if(getContext() != null )
        if(!PermissionUtils.checkLocationPermission(getContext()))return;
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        otherUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                User  user =  dataSnapshot.getValue(User.class);

                assert user != null;
                double lat = parseDouble(user.getLatitude());
                double lon = parseDouble(user.getLongitude());

                if( latitude != lat && longitude != lon ){
                    if(enemyMarker == null) return;
                    enemyMarker.remove();
                    setChallengerMarkerLocaTion(gMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        assert location != null;
        fromUserRef.child(firebaseUser.getUid()).child("latitude").setValue(String.valueOf(location.getLatitude()));
        fromUserRef.child(firebaseUser.getUid()).child("longitude").setValue(String.valueOf(location.getLongitude()));


    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void requestLocationUpdate(){


        if( ! PermissionUtils.checkLocationPermission(getContext())){
            PermissionUtils.requestLocationPermission(getActivity(),MY_PERMISSION_REQUEST_CODE);
            return;
        }


        createLocationRequest();

        apiClient.requestLocationUpdates(mLocationRequest,locationCallback , null);//  null => looper :  get the result on the main thread


    }

    private void createLocationRequest() {

        mLocationRequest = LocationRequest.create();
        // priority will influence the use of the gps and the battery, the more accurate it is
        // the more the demand in power will be great
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                // turn it on every 5 seconds
                .setInterval(1000*5)
                // if you already have the location update ever 0.5s
                .setFastestInterval(500);
    }

    private LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if( locationResult == null) return;
            apiClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(final Location location) {
                    if (location != null) {
                        mCurrentlocation = location ;
                    }

                }
            });

        }

    };


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {

        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            assert vectorDrawable != null;
            vectorDrawable = (DrawableCompat.wrap(vectorDrawable)).mutate();
            vectorDrawable.getConstantState();
        }

        assert vectorDrawable != null;
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }




    @Override
    public void onLocationChanged(Location location) {

        if(location != mCurrentlocation){
            mCurrentlocation = location ;
            if(getContext() != null)
            updateFirebaseLocation();

        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onConnected(@Nullable Bundle bundle) {
            //requestLocationUpdate();
    }



    @Override
    public void onConnectionSuspended(int i) {
       mGoogleApiClient.disconnect();


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(requestCode ==  MY_PERMISSION_REQUEST_CODE  && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            requestLocationUpdate();
            mapSetup(gMap);
            }

        }

    @Override
    public void onDirectionFinderStart() {

        if (polyLinePaths != null) {
            for (Polyline polylinePath : polyLinePaths) {
                polylinePath.remove();
            }
        }

    }


    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {

            polyLinePaths = new ArrayList<>();
            for (Route route : routes) {
                PolylineOptions polylineOptions = new PolylineOptions()
                        .geodesic(true)
                        .color(Color.BLUE)
                        .width(10);


                for (int i = 0; i < route.points.size(); i++) {
                    polylineOptions.add(route.points.get(i));
                }

                polyLinePaths.add(gMap.addPolyline(polylineOptions));
            }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        gMap =googleMap ;

            requestLocationUpdate();

            apiClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());

               new ParsingApi(getContext()).loadUrl(new VolleyCallBack(){
                   @Override
                   public void onSuccess(ArrayList<Places> places) {

                       barArrayList = places ;
                       startLocationUpdates();

                       final ProgressDialog progressDialog = new ProgressDialog(getContext());
                       progressDialog.setTitle("Loading");
                       progressDialog.show();
                       Timer timer = new Timer();
                       TimerTask task = new TimerTask() {
                           @Override
                           public void run() {

                               getActivity().runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {

                                       mapSetup(googleMap);
                                       progressDialog.dismiss();

                                   }
                               });

                           }
                       };
                       timer.schedule(task,2000);




                   }
               });



    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        FragmentTransaction transaction = null;

        assert getFragmentManager() != null;
        SupportMapFragment mapFragment =  (SupportMapFragment)getFragmentManager().findFragmentById(R.id.map_fragment);
        if(mapFragment != null){
            assert false;
            transaction.remove(mapFragment).commit();
        }

    }
}

