package com.rubenmimoun.beerchallenge.FragmentAcvities;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rubenmimoun.beerchallenge.Adapter.MessageAdapter;
import com.rubenmimoun.beerchallenge.ChallengeActivities.ActivityChallengeOne;
import com.rubenmimoun.beerchallenge.MainActivity;
import com.rubenmimoun.beerchallenge.Models.Chat;
import com.rubenmimoun.beerchallenge.Models.User;
import com.rubenmimoun.beerchallenge.Notification.Data;
import com.rubenmimoun.beerchallenge.Notification.Sender;
import com.rubenmimoun.beerchallenge.Notification.Token;
import com.rubenmimoun.beerchallenge.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {


    private CircleImageView profile_pic;
    private TextView username ;
    private ImageButton send_btn ;
    private EditText text_send ;

    private FirebaseUser firebaseUser ;
    private DatabaseReference referenceChallenger;
    private DatabaseReference referenceUser ;

    private RecyclerView recyclerView ;
    private MessageAdapter adapter ;
    private List<Chat> chatList ;

    private ValueEventListener seenListener  ;
    private boolean notify = false ;

    private Bundle bundle ;

    User challenger ;

    MediaPlayer mp ;


    public ChatFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_chat, container, false);


        mp = MediaPlayer.create(getContext(), R.raw.blop);


        recyclerView =  v.findViewById(R.id.recycler_view_chat_frag) ;
        LinearLayoutManager layoutManager =  new LinearLayoutManager(getContext()) ;
        recyclerView.setHasFixedSize(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);


        send_btn=v.findViewById(R.id.send_msg_btn);
        text_send=v.findViewById(R.id.msg_to_send);
        username=v.findViewById(R.id.username_message);
        profile_pic =v.findViewById(R.id.profil_image_message);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;


        referenceUser = FirebaseDatabase.getInstance().getReference(MainActivity.USERS).child(firebaseUser.getUid());
        //referenceUser.child("AppsON");
        referenceChallenger = FirebaseDatabase.getInstance().getReference(MainActivity.USERS).child(ActivityChallengeOne.OTHERUSER);

        referenceChallenger.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(isAdded()){

                    User user = dataSnapshot.getValue(User.class);

                    username.setText(user.getName());

                    if(user.getImageURL().equals("default")){
                        profile_pic.setImageResource(R.drawable.unknown);
                    }else{
                        Glide.with(getActivity()).load(user.getImageURL()).into(profile_pic);

                    }

                    readMessage(firebaseUser.getUid(), ActivityChallengeOne.OTHERUSER, user.getImageURL());
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }



        });

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify= true ;

                String message =  text_send.getText().toString() ;


                if(!message.equals("")){

                    sendMessage(firebaseUser.getUid(), ActivityChallengeOne.OTHERUSER, message);
                }else{
                    Toast.makeText(getActivity(),
                            "You cannot send an empty message", Toast.LENGTH_SHORT).show();
                }

                text_send.setText("");

            }
        });

        seenMessage(ActivityChallengeOne.OTHERUSER);



        return  v ;
    }


    private void seenMessage(final String userid){


        referenceChallenger = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = referenceChallenger.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat =  snapshot.getValue(Chat.class);

                    assert chat != null;
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)){
                        HashMap<String, Object> hashMap = new HashMap<>() ;
                        hashMap.put("isseen", true) ;
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void sendMessage(String sender , final String receiver, String message){


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("isseen",false);

        reference.child("Chats").push().setValue(hashMap);


        final DatabaseReference chatRef =  FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(ActivityChallengeOne.OTHERUSER);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if( !dataSnapshot.exists()){
                    chatRef.child("id").setValue(ActivityChallengeOne.OTHERUSER);
                    createNotification();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String msg = message ;

        reference = FirebaseDatabase.getInstance().getReference(MainActivity.USERS).child(firebaseUser.getUid()) ;
        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class) ;
                if( notify){

                    sendNotification(receiver, user.getName(), msg);


                }

                notify = false ;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void sendNotification(String receiver, final String username, final String msg){



        DatabaseReference tokens =  FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object>hashMap = new HashMap<>() ;

        hashMap.put("receiver", receiver);
        hashMap.put("username", username);
        hashMap.put("token",msg);

        tokens.child("Tokens").setValue(hashMap);

        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for ( DataSnapshot snapshot : dataSnapshot.getChildren()){



                    Token token = snapshot.getValue(Token.class);

                    Data data = new Data
                            (firebaseUser.getUid(),
                                    R.mipmap.ic_launcher,
                                    username +":"+msg,
                                    "New message",
                                    ActivityChallengeOne.OTHERUSER);

                    Sender sender =new Sender(data, token.getToken());


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessage(final String myid, final String userid, final String imageurl){

        chatList = new ArrayList<>();
        referenceChallenger =  FirebaseDatabase.getInstance().getReference("Chats") ;
        referenceChallenger.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Chat chat =  snapshot.getValue(Chat.class) ;

                    assert chat != null;
                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid)
                            || chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        chatList.add(chat);


                    }




                }

                adapter =  new MessageAdapter(getContext(), chatList,imageurl);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    synchronized
    private void createNotification(){

        final NotificationManager notificationManager =
                (NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE);

        final Intent notificationIntent = new Intent(getActivity(),ActivityChallengeOne.class);

        final PendingIntent notificationPendingIntent =
                PendingIntent.getActivity(getActivity(),
                        1,
                        notificationIntent,
                        PendingIntent.FLAG_ONE_SHOT);

        Notification mBuilder = new NotificationCompat.Builder(getContext())
                .setWhen(System.currentTimeMillis())
                .setTicker("New Notification")
                .setSmallIcon(R.drawable.ic_local_bar)
                .setContentIntent(notificationPendingIntent)
                .build();


    }


    @Override
    public void onDestroy() {
        super.onDestroy();



    }
}
