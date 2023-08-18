package com.example.findmyroom.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.appbroker.roundedimageview.RoundedImageView;
import com.example.findmyroom.ImageViewActivity;
import com.example.findmyroom.MainActivity;
import com.example.findmyroom.Models.LikedRoomModel;
import com.example.findmyroom.Models.MainActivityModel;
import com.example.findmyroom.R;
import com.example.findmyroom.frontend.SplashActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class LikeRoomAdapter extends RecyclerView.Adapter<LikeRoomAdapter.viewHolder> {
    Context context;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    ArrayList<LikedRoomModel> roomArray = new ArrayList<LikedRoomModel>();
    LikedRoomModel likedRoomModel = new LikedRoomModel();

    public LikeRoomAdapter(Context context, ArrayList<LikedRoomModel> roomArray) {
        this.context = context;
        this.roomArray = roomArray;

    }

    @NonNull
    @Override
    public LikeRoomAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.room_cardview,parent,false);
        return new LikeRoomAdapter.viewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull LikeRoomAdapter.viewHolder holder, int position) {
        likedRoomModel = roomArray.get(position);

        Picasso.get().load(likedRoomModel.getCoverimage()).into(holder.coverimage);

        holder.address.setText(likedRoomModel.getAddress());
        holder.phonenumber.setText(likedRoomModel.getPhonenumber().substring(0,5));
        holder.price.setText(likedRoomModel.getPrice());
        holder.rentername.setText(likedRoomModel.getRentername());
        holder.like.setText("Delete");
        holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.delete_24, 0, 0, 0);

        //phone call
        holder.phonenumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + roomArray.get(position).getPhonenumber()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

       //ldelete
        holder.like.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Toast.makeText(context, "deleting...", Toast.LENGTH_SHORT).show();
                        DatabaseReference databaseReference = roomArray.get(position).getRoomReference();
                        firebaseDatabase.getReference().child("PG").child(firebaseAuth.getUid()).child("LikedRooms").child(databaseReference.getKey().toString()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                            }
                        });


                return true;
            }
        });
        //imageview
        holder.coverimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ImageViewActivity.class);
                intent.putExtra("imageRef", roomArray.get(position).getImageReference());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });




    }

    @Override
    public int getItemCount() {
        return roomArray.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView address, price, rentername ,like;
        Button phonenumber;
        RoundedImageView coverimage;



        public viewHolder(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address);
            price = itemView.findViewById(R.id.price);
            rentername= itemView.findViewById(R.id.provider) ;
            phonenumber = itemView.findViewById(R.id.contactcall);
            coverimage= itemView.findViewById(R.id.roundedImageView);
            like    = itemView.findViewById(R.id.like) ;

        }
    }
}
