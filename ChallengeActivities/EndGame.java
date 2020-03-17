package com.rubenmimoun.beerchallenge.ChallengeActivities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rubenmimoun.beerchallenge.FragmentAcvities.ChatFragment;
import com.rubenmimoun.beerchallenge.MainActivity;
import com.rubenmimoun.beerchallenge.R;

public class EndGame extends AppCompatActivity {

    ImageView beer ;
    TextView gameover ;
    TextView timerOn ;
    TextView go ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        beer = (ImageView)findViewById(R.id.beerImg);
        gameover = (TextView)findViewById(R.id.gameOvertxt);

        timerOn = (TextView)findViewById(R.id.timerOn);
        go = (TextView)findViewById(R.id.go);

        go.setTranslationY(-1000);
        timerOn.setTranslationY(-1000);


        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(MainActivity.USERS);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        animateElements();
    }


    private void animateElements(){

        beer.setTranslationY(-1000);
        beer.animate().translationY(-300).rotation(360).setDuration(2000);
        gameover.setTranslationY(1000);
        gameover.animate().translationY(0).rotation(-360).setDuration(2000);


    }

    public void startTimer(View view) {

        if(view.getId() == R.id.timerOn){
            beer.animate().scaleX(1.3f).scaleY(1.3f).setDuration(1000);
            timerOn.animate().translationY(-1000).setDuration(200);
            gameover.animate().translationY(1000).setDuration(2000);
            go.animate().translationY(-150).setDuration(2000);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container,new ChatFragment() )
                    .commit();

            getTimer();

        }



    }


    private void getTimer() {
            Toast.makeText(getApplicationContext(), "30s from now !", Toast.LENGTH_SHORT).show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Times up ! ", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
                        startActivity(intent1);
                    }
                });

            }
        },10*3000);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        finishAndRemoveTask();
    }
}
