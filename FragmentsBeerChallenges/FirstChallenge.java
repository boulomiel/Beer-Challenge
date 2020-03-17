package com.rubenmimoun.beerchallenge.FragmentsBeerChallenges;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rubenmimoun.beerchallenge.MainActivity;
import com.rubenmimoun.beerchallenge.MainMenu;
import com.rubenmimoun.beerchallenge.R;
import com.rubenmimoun.beerchallenge.SearchingActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class FirstChallenge extends Fragment {

    private DatabaseReference myRef ;

    private TextView title ;
    private TextView explanation ;
    private TextView swipe ;
    private Button start_first ;
    private ImageView arrow ;
    private ImageView imageView ;
    private ImageView hand ;

    private boolean outTheScreen ;

    public FirstChallenge() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.first_challenge_fragment, container, false);
        title = v.findViewById(R.id.title);
        explanation=v.findViewById(R.id.explanation);
        arrow=v.findViewById(R.id.arrow);
        start_first = v.findViewById(R.id.btnstart_1);
        imageView =v.findViewById(R.id.imageView);
        hand = v.findViewById(R.id.hand_);
        swipe =  v.findViewById(R.id.swipe);

        swipe.setScaleX(0.5f);
        swipe.setScaleY(0.5f);
        imageView.setScaleX(0.1f);
        imageView.setScaleY(0.1f);
        title.setTranslationX(1000);
        explanation.setTranslationX(1000);
        arrow.setScaleX(0.1f);
        arrow.setScaleY(0.1f);
        start_first.setTranslationX(-1000);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        myRef= FirebaseDatabase.getInstance().getReference(MainActivity.USERS).child(firebaseUser.getUid());

        outTheScreen = false ;

        start_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewsOutMovement();

                Timer timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        if(outTheScreen){
                            setChallenge();
                            Intent intent  = new Intent(getActivity(), SearchingActivity.class);
                            intent.putExtra("activity",MainMenu.CHALLENGE1);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK) ;
                            startActivity(intent);
                            outTheScreen= false ;
                        }
                    }
                };
                timer.schedule(timerTask,350);

            }
        });

        viewsInMovement();

        return  v ;
    }


    private void viewsInMovement(){


        title.animate().translationX(0).setDuration(1000);
        start_first.animate().translationX(0).setDuration(1000);
        explanation.animate().translationX(0).setDuration(1000);
        arrow.animate().scaleX(1f).scaleY(1f).setDuration(1000);
        imageView.animate().scaleX(1f).scaleY(1f).setDuration(1000);
        hand.animate().translationX(-800).translationY(2).setDuration(1000);
        swipe.animate().scaleX(2.5f).scaleY(3.5f).setDuration(2500);
        swipe.animate().translationX(-700).setDuration(3000);

    }





    private void viewsOutMovement(){

      if(!outTheScreen){
          title.animate().translationX(1500).setDuration(500).start();
          start_first.animate().translationY(2000).setDuration(500).start();
          arrow.animate().scaleX(0.1f).scaleY(0.1f).translationX(-1500).setDuration(500).start();
          explanation.animate().scaleY(0.01f).translationY(2000).setDuration(500).start();
          imageView.animate().scaleX(1000f).scaleY(0f).setDuration(350).start();

          outTheScreen = true ;
      }
    }




     private void setChallenge(){
         myRef.child("challenge").setValue(MainMenu.CHALLENGE1);

    }

}
