package com.bhjbestkalyangame.realapplication.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bhjbestkalyangame.realapplication.ChattingActivity;
import com.bhjbestkalyangame.realapplication.Model.Chat;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.bhjbestkalyangame.realapplication.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    FirebaseUser mFirebaseUser;
    private Context mContext;
    private List<Chat> mChat;
    private String mImageUrl;

    public MessageAdapter(Context mContext, List<Chat> mChat, String mImageUrl) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.mImageUrl = mImageUrl;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            mImageUrl = "https://firebasestorage.googleapis.com/v0/b/childcareapp-e9675.appspot.com/o/Uploads%2F1604329873197.png?alt=media&token=8de870fa-39e5-44e1-b32d-bb8c41d7f469";
            return new ViewHolder(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            mImageUrl = "https://firebasestorage.googleapis.com/v0/b/childcareapp-e9675.appspot.com/o/Uploads%2F1604332064892.png?alt=media&token=dc8829b6-ec8c-46b0-9e06-6c08fd65ba90";
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Chat chat = mChat.get(position);

        holder.ShowMessage.setText(chat.getMessage());

        if(mImageUrl.equals("default")){
            holder.ProfileImage.setImageResource(R.mipmap.ic_launcher);
        }else{
          Glide.with(mContext).load(mImageUrl).into(holder.ProfileImage);
        }


        if(position == mChat.size()-1){
            if(chat.isSeen()){
                holder.TextSeen.setText("seen");
            }else{
                holder.TextSeen.setText("delivered");
            }
        }else {
            holder.TextSeen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        public TextView ShowMessage;
        public ImageView ProfileImage;
        public TextView TextSeen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ShowMessage = itemView.findViewById(R.id.show_message);
            ProfileImage = itemView.findViewById(R.id.profile_image);
            TextSeen = itemView.findViewById(R.id.text_seen);
        }


    }

    @Override
    public int getItemViewType(int position) {

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            if (mChat.get(position).getSender().equals(mFirebaseUser.getUid())) {
                return MSG_TYPE_RIGHT;
            } else {
                return MSG_TYPE_LEFT;
            }

    }

}
