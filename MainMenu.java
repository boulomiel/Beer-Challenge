package com.rubenmimoun.beerchallenge;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.rubenmimoun.beerchallenge.FragmentAcvities.MAPFragment;
import com.rubenmimoun.beerchallenge.FragmentsBeerChallenges.FirstChallenge;
import com.rubenmimoun.beerchallenge.Models.User;

import java.util.ArrayList;
import java.util.HashMap;

public class MainMenu extends AppCompatActivity {

    public static double lat, lon ;
    public static final String CHALLENGE1 ="challenge 1";
    public static  String USERID;

    private DatabaseReference mRef ;
    private FirebaseUser firebaseUser ;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                System.out.println(currentUser == null);
                if (currentUser == null) {
                    //Here is the place to redirect the AppUser to LoginActivity
                    mRef.child("status").setValue("offline");
                    Toast.makeText(getApplicationContext(),"A challenger left", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainMenu.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                }else{
                    mRef.getDatabase().goOnline();


                }

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");



        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mRef= FirebaseDatabase.getInstance().getReference(MainActivity.USERS).child(firebaseUser.getUid());
        USERID = firebaseUser.getUid() ;


        setStatus("online");
        inGame("no");
        setChallenge("");
        changePlayingStatus("no");
        cutConnectionToOthers();


//        requestPermissions(new String[]{
//                Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION }, MAPFragment.MY_PERMISSION_REQUEST_CODE);


        ViewPager viewPager = findViewById(R.id.pager_challenge);

        PickAChallengePagerAdapter pagerAdapter = new PickAChallengePagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragmet(new FirstChallenge());
        pagerAdapter.addFragmet(new Profile());


        viewPager.setAdapter(pagerAdapter);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true ;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.logout :
                FirebaseAuth.getInstance().signOut();
                mRef.child("status").setValue("offline");
                mRef.child("playing").setValue("no");
                mRef.child("challenge").setValue("");
                mRef.child("bars").removeValue();
                startActivity(new Intent(MainMenu.this, MainActivity.class).
                        setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK));
                return  true ;
        }
            return  false ;
    }

    class PickAChallengePagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragmentList = new ArrayList<>();

        public PickAChallengePagerAdapter(FragmentManager fm) {
            super(fm);
        }

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

        void addFragmet(Fragment fragment) {
            fragmentList.add(fragment);
        }
    }



    private void setStatus( String status){

        HashMap<String, Object> hashMap = new HashMap<>() ;
        hashMap.put("status",status) ;

        mRef.updateChildren(hashMap) ;
    }

    private void setChallenge( String challenge){

        HashMap<String, Object> hashMap = new HashMap<>() ;
        hashMap.put("challenge",challenge) ;

        mRef.updateChildren(hashMap) ;
    }

    private void inGame( String inGame){

        HashMap<String, Object> hashMap = new HashMap<>() ;
        hashMap.put("inGame",inGame) ;

        mRef.updateChildren(hashMap) ;
    }

    private void changePlayingStatus( String isPlaying){

        HashMap<String, Object>  playing = new HashMap<>() ;
        playing.put("playing", isPlaying);

        mRef.updateChildren(playing);
    }

    private void cutConnectionToOthers(){
        HashMap<String, Object> hashMap = new HashMap<>() ;
        hashMap.put("connected_to","") ;

        mRef.updateChildren(hashMap) ;

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class) ;
                if(user.getLeft() == null) return ;

                mRef.child("left").removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


            }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && requestCode ==  MAPFragment.MY_PERMISSION_REQUEST_CODE  && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            PermissionUtils.checkLocationPermission(this);
            PermissionUtils.requestLocationPermission(this, MAPFragment.MY_PERMISSION_REQUEST_CODE);
        }

    }


}
