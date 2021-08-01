package com.bhjbestkalyangame.realapplication;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class ViewHolder extends RecyclerView.ViewHolder {

    View view;

    public ViewHolder(View view) {
        super(view);
        this.view = view;
    }

    public void setDetails(Context context, String title, String description, String image){

        TextView mName = view.findViewById(R.id.story_title);
        TextView mDescription = view.findViewById(R.id.story_description);
        ImageView mImage = view.findViewById(R.id.image_view_upload);


        mName.setText(title);
        mDescription.setText(description);
        Picasso.get().load(image).into(mImage);

        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        view.setAnimation(animation);

    }


}
