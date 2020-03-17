package com.rubenmimoun.beerchallenge;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rubenmimoun.beerchallenge.ChallengeActivities.ActivityChallengeOne;
import com.rubenmimoun.beerchallenge.Models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class SearchingActivity extends AppCompatActivity {

    ProgressDialog progressDialog ;
    Button cancel_search ;

    FirebaseUser firebaseUser;
    DatabaseReference challengerRef;
    DatabaseReference personalRef;
    ArrayList<User>userArrayList ;
    String yes  = "yes" ;
    Timer timer  ;

    public  String challengerId;
    private  TimerTask task ;

    private int updateCounts = 0 ;
    private int  time = 3 ;

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        cancel_search = findViewById(R.id.cancel_search);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        personalRef = FirebaseDatabase.getInstance().getReference(MainActivity.USERS).child(firebaseUser.getUid());

        changePlayingStatus(yes);
        setStatus("online");


        cancel_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                leaveReseach();

            }
        });




        timer = new Timer();
        userArrayList = new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Looking for a challenger ! ");
        progressDialog.setMessage("Please wait ... ");

        progressDialog.show();


        updateSearchs();


    }

    private void leaveReseach(){

        setStatus("offline");
        changePlayingStatus("no");
        startActivity(new Intent(SearchingActivity.this, MainMenu.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        timer.cancel();

    }




    private void getOthersChallengers(){



        challengerRef = FirebaseDatabase.getInstance().getReference(MainActivity.USERS);

        challengerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    assert user != null;

                        if(!user.getId().equals(MainMenu.USERID)
                                && user.getStatus().equals("online")
                                && (user.getConnected_to().equals("") || user.getConnected_to().equals(MainMenu.USERID))
                                && user.getPlaying().equals(yes))
                        {
                            userArrayList.add(user);

                            pickAChallenger();

                        }

                }

            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }




    private void pickAChallenger(){


        Random ran =  new Random();
        int picked = ran.nextInt( userArrayList.size() +1);

        for (int i = 0; i <userArrayList.size() ; i++) {
            if( i == picked  ){

                challengerId = userArrayList.get(i).getId() ;
                timer.cancel();
                task.cancel();
                goToNextActivity(challengerId);


            }
        }

    }




    private void updateSearchs(){

        long delay = 0 ;
        final long period = 3000 ;

             task  = new TimerTask() {
                @Override
                public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = time; i > 0 ; i--) {
                                    time-- ;
                                    getOthersChallengers();

                                    if(time <= 0 ){
                                        updateCounts ++;
                                        // updateSearchs();
                                        time = 3 ;
                                        if(updateCounts == 10){
                                            Toast.makeText(getApplicationContext(),
                                                    "We currently can't find you a challenger, try again later", Toast.LENGTH_SHORT).show();
                                            timer.cancel();
                                            leaveReseach();
                                        }
                                    }

                                }
                            }
                        });


                }
            };
            timer.scheduleAtFixedRate(task, delay, period);




    }


    private void goToNextActivity(String challengerid){



        try {
            if ((progressDialog != null) && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (final IllegalArgumentException e) {
            // Handle or log or ignore
        } catch (final Exception e) {
            // Handle or log or ignore
        } finally {
            progressDialog = null;
        }


        personalRef.child("connected_to").setValue(challengerid);
        challengerRef = FirebaseDatabase.getInstance().getReference(MainActivity.USERS).child(challengerid) ;
        challengerRef.child("connected_to").setValue(firebaseUser.getUid()) ;

        Intent intent ;

                intent  = new Intent(SearchingActivity.this, ActivityChallengeOne.class);
                intent.putExtra("otherid",challengerid) ;
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                userArrayList.clear();
                startActivity(intent);

                finish();

    }




    private void changePlayingStatus( String isPlaying){

        HashMap<String, Object>  playing = new HashMap<>() ;
        playing.put("playing", isPlaying);

        personalRef.updateChildren(playing);
    }


    private void setStatus( String status){

        HashMap<String, Object> hashMap = new HashMap<>() ;
        hashMap.put("status",status) ;

        personalRef.updateChildren(hashMap) ;
    }



    @Override
    protected void onResume() {
        super.onResume();
        changePlayingStatus(yes);
    }



}
