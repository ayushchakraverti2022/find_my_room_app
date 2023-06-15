package com.example.findmyroom.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appbroker.roundedimageview.RoundedImageView;
import com.example.findmyroom.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;
import java.util.Arrays;

public class AddnewRoomAdapter extends RecyclerView.Adapter<AddnewRoomAdapter.viewHolder>{

    ArrayList<Uri> images = new ArrayList<>();
    Context context;

    public AddnewRoomAdapter(ArrayList<Uri> images, Context context) {
        this.images = images;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_room_layout_addnewroom,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
             holder.roundedImageView.setImageURI(images.get(position));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        RoundedImageView roundedImageView;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            roundedImageView = itemView.findViewById(R.id.addnewroomimageview);

        }
    }
}
