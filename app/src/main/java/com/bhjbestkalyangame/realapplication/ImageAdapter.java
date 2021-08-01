package com.bhjbestkalyangame.realapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context mContext;
    private List<Upload> mUploads;
    private  Picasso mPicasso;

    public ImageAdapter(Context context, List<Upload> uploads, Picasso mPicasso) {
        mContext = context;
        mUploads = uploads;
        this.mPicasso = mPicasso;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Upload uploadCurrent = mUploads.get(position);
        holder.Title.setText(uploadCurrent.getTitle());
        holder.Description.setText(uploadCurrent.getDescription());
        mPicasso.get()
                .load(uploadCurrent.getImageUrl())
                .into(holder.ImageView);

//          .placeholder(R.mipmap.ic_launcher)
//                .fit()
//                .centerCrop()
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }
    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView Title, Description;
        public ImageView ImageView;
        public ImageViewHolder(View itemView) {
            super(itemView);

            Title = itemView.findViewById(R.id.story_title);
            Description = itemView.findViewById(R.id.story_description);
            ImageView = itemView.findViewById(R.id.image_view_upload);
        }
    }
}