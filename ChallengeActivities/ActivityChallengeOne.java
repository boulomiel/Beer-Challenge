package com.rubenmimoun.beerchallenge.ChallengeActivities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rubenmimoun.beerchallenge.FragmentAcvities.ChatFragment;
import com.rubenmimoun.beerchallenge.FragmentAcvities.MAPFragment;
import com.rubenmimoun.beerchallenge.MainActivity;
import com.rubenmimoun.beerchallenge.MainMenu;
import com.rubenmimoun.beerchallenge.Models.User;
import com.rubenmimoun.beerchallenge.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class ActivityChallengeOne extends AppCompatActivity {


    PickAFragmentInActivityPager pagerAdapter ;
    FirebaseUser firebaseUser;
    public static String OTHERUSER ;
    private Handler handler ;
    DatabaseReference challengerRef ;

    DatabaseReference mRef ;
    private TextView swipe;


    @Override
    protected void onStart() {
        super.onStart();

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                swipe.setScaleX(0.5f);
                swipe.setScaleY(0.5f);
                //hand.animate().translationX(-1000).translationY(2).setDuration(1000);
                swipe.animate().scaleX(2.5f).scaleY(3.5f).setDuration(2500);
                swipe.animate().translationX(-1300).setDuration(3000);
            }
        };
        timer.schedule(task,5000);



    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_one);

      // hand = (ImageView)findViewById(R.id.hand_map);
       swipe = (TextView) findViewById(R.id.swipe_map);
//




        Toolbar toolbar = findViewById(R.id.toolbar_one);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
// get data via the key
        Bundle bundle = new Bundle();
        ChatFragment chatFragment = new ChatFragment();
        String challengerid = extras.getString("otherid");
        if (challengerid != null) {
            // do something with the data
            OTHERUSER = challengerid ;
            bundle.putString("otherid",challengerid);
            chatFragment.setArguments(bundle);
        }


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        mRef = FirebaseDatabase.getInstance().getReference("users").child(MainMenu.USERID);
         challengerRef = FirebaseDatabase.getInstance().getReference("users").child(OTHERUSER);


        ViewPager viewPager = findViewById(R.id.viewPager_challenge1);
        pagerAdapter = new PickAFragmentInActivityPager(getSupportFragmentManager());
        pagerAdapter.addFragment(new MAPFragment());
        pagerAdapter.addFragment(new ChatFragment());



        viewPager.setAdapter(pagerAdapter);


        handler = new Handler();
        handler.post(sendData);

    }


    final Runnable sendData = new Runnable(){
        public void run(){
            try {
                //prepare and send the data here..

                FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                            checkOtherChallengersConnection();

                    }
                });
                handler.postDelayed(this, 1000);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activities,menu);
        return true ;
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch ((item.getItemId())){
            case R.id.back_to_menu :
                leftGame();
                return true ;
        }


        return  false ;

    }


    private void leftGame(){


        new AlertDialog.Builder(this)
                .setMessage("Want to leave you new best mate,"+"\n"+"\n"+" you will be disconnected ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cutConnectionToOthers(mRef);
                                changePlayingStatus(mRef,"no");
                                setStatus(mRef,"offline");
                                mRef.child("left").setValue("yes");
                                Intent intent = new Intent(ActivityChallengeOne.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
                                FirebaseAuth.getInstance().signOut();
                                startActivity(intent);
                                handler.removeCallbacks(sendData);
                                ActivityChallengeOne.super.onBackPressed();
                                finish();
                            }
                        })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return ;

                    }
                })
                .create()
                .show();

    }

    @Override
    public void onBackPressed() {
        leftGame();
    }

    public void checkOtherChallengersConnection(){


            challengerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    final User user = dataSnapshot.getValue(User.class);

                        if(user.getLeft() == null) return ;

                         if( user.getLeft().equals("yes")) {

                        cutConnectionToOthers(mRef);
                        changePlayingStatus(mRef,"no");
                        setStatus(mRef,"offline");
                        Intent intent = new Intent(ActivityChallengeOne.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
                        startActivity(intent);
                        handler.removeCallbacks(sendData);
                        finish();


                    }

                    }




                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




    }




    class PickAFragmentInActivityPager extends FragmentPagerAdapter {

        public ArrayList<Fragment> fragmentList = new ArrayList<>();




        PickAFragmentInActivityPager(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int i) {

            return fragmentList.get(i);

        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            // without supercall the fragment will not be destroyed , and will be reused
        }

        void addFragment(Fragment fragment) {
            fragmentList.add(fragment);
        }
    }



    private void setStatus(DatabaseReference ref, String status){

        HashMap<String, Object> hashMap = new HashMap<>() ;
        hashMap.put("status",status) ;

        ref.updateChildren(hashMap) ;
    }



    private void changePlayingStatus( DatabaseReference ref,String isPlaying){

        HashMap<String, Object>  playing = new HashMap<>() ;
        playing.put("playing", isPlaying);

        ref.updateChildren(playing);
    }

    private void cutConnectionToOthers(DatabaseReference ref){

        HashMap<String, Object>  connected_to = new HashMap<>() ;
        connected_to.put("connected_to", "");

        ref.updateChildren(connected_to);
    }



    @Override
    protected void onDestroy() {

        setStatus(mRef,"offline");
        changePlayingStatus(mRef,"");
        cutConnectionToOthers(mRef);
        mRef.getDatabase().goOffline();
        FirebaseAuth.getInstance().signOut();
        handler.removeCallbacks(sendData);

        super.onDestroy();




    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }




}
