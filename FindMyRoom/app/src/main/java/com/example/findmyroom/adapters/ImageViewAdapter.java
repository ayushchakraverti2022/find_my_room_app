package com.example.findmyroom.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.HalfFloat;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appbroker.roundedimageview.RoundedImageView;
import com.example.findmyroom.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class ImageViewAdapter extends RecyclerView.Adapter<ImageViewAdapter.viewHolder>{
    ArrayList<String> images = new ArrayList<>();
    Context context;

    public ImageViewAdapter(ArrayList<String> images, Context context) {
        this.images = images;
        this.context = context;
    }

    @NonNull
    @Override
    public ImageViewAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.imageview_recyclerview,parent,false);
        return new ImageViewAdapter.viewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ImageViewAdapter.viewHolder holder, int position) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Picasso.get().load(images.get(position)).into(holder.roundedImageView);
            }
        });

    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        PhotoView roundedImageView;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            roundedImageView = itemView.findViewById(R.id.addnewroomimageview);

        }
    }
}
