package com.rubenmimoun.beerchallenge.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rubenmimoun.beerchallenge.Models.Chat;
import com.rubenmimoun.beerchallenge.R;

import java.lang.ref.WeakReference;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter  extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_LEFT = 0 ;
    public static final int MSG_RIGHT = 1 ;

    private WeakReference<Context> mContext ;
    private List<Chat> chatList;
    private String imageurl ;

    FirebaseUser firebaseUser ;

    public MessageAdapter(Context context, List<Chat>list, String imageurl){
        this.mContext = new WeakReference<>(context) ;
        this.chatList =  list ;
        this.imageurl = imageurl ;

    }



    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if( viewType == MSG_RIGHT){
            View v  =  LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right,parent, false);

            return new MessageAdapter.ViewHolder(v) ;
        }else{
            View  v  =  LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left,parent, false);
            return new MessageAdapter.ViewHolder(v) ;
        }

    }



    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        final Chat  chat = chatList.get(position) ;

        holder.show_message.setText(chat.getMessage());

        if(imageurl.equals("default")){
            holder.profil_pic.setImageResource(R.drawable.unknown);
        }else{
            Glide.with(mContext.get()).load(imageurl).into(holder.profil_pic);
        }

        // check if its the last message, otherwise the textView seen is invisible
        if( position == chatList.size()-1){

            if(chat.isIsseen()){

                holder.txt_seen.setText("Seen");
            }else {

                holder.txt_seen.setText("Delivered");
            }

        }else{
            holder.txt_seen.setVisibility(View.GONE);
        }



    }


    @Override
    public int getItemCount() {
        return chatList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message ;
        public CircleImageView profil_pic ;
        public  TextView txt_seen ;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message) ;
            profil_pic =  itemView.findViewById(R.id.profil_image_item);
            txt_seen = itemView.findViewById(R.id.txt_seen);
        }

    }

    @Override
    public int getItemViewType(int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        if(chatList.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_RIGHT ;
        }else{
            return MSG_LEFT ;

        }
    }

}