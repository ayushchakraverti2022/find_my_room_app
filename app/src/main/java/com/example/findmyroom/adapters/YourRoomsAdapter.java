package com.example.findmyroom.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appbroker.roundedimageview.RoundedImageView;
import com.example.findmyroom.ImageViewActivity;
import com.example.findmyroom.Models.MainActivityModel;
import com.example.findmyroom.Models.YourRoomsModel;
import com.example.findmyroom.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class YourRoomsAdapter extends  RecyclerView.Adapter<YourRoomsAdapter.viewHolder>{
    Context context;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    public boolean checkNetwork(){

        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);

        return  connected;
    };
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    ArrayList<YourRoomsModel> roomArray = new ArrayList<YourRoomsModel>();
    YourRoomsModel yourRoomsModel = new YourRoomsModel();

    public YourRoomsAdapter(Context context, ArrayList<YourRoomsModel> roomArray) {
        this.context = context;
        this.roomArray = roomArray;


    }

    @NonNull
    @Override
    public YourRoomsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.room_cardview,parent,false);
        return new YourRoomsAdapter.viewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull YourRoomsAdapter.viewHolder holder, int position) {
        yourRoomsModel = roomArray.get(holder.getAdapterPosition());

        Picasso.get().load(yourRoomsModel.getCoverimage()).into(holder.coverimage);

        holder.address.setText(yourRoomsModel.getAddress());
        holder.phonenumber.setText(yourRoomsModel.getPhonenumber().substring(0,5));
        holder.price.setText(yourRoomsModel.getPrice());
        holder.rentername.setText(yourRoomsModel.getRentername());
        holder.like.setText("Delete");
        holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.delete_24, 0, 0, 0);

        //ldelete
        holder.like.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Toast.makeText(context, "deleting...", Toast.LENGTH_SHORT).show();
                DatabaseReference databaseReference = roomArray.get(holder.getAdapterPosition()).getRoomReference();
                firebaseDatabase.getReference().child("Renter").child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String roomcount = Long.toString((Long) snapshot.child("RoomCount").getValue());
                        String roomname = databaseReference.getKey().toString();
                        firebaseDatabase.getReference().child("Renter").child(firebaseAuth.getUid()).child("Rooms").child(databaseReference.getKey().toString()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                firebaseStorage.getReference().child(firebaseAuth.getUid()).child(roomname).delete();
                                int rc = Integer.parseInt(roomcount);
                                firebaseDatabase.getReference().child("Renter").child(firebaseAuth.getUid()).child("RoomCount").setValue(rc);

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



                return true;
            }
        });

        //phone call
        holder.phonenumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + roomArray.get(holder.getAdapterPosition()).getPhonenumber()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });



        //imageview
        holder.coverimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ImageViewActivity.class);
                intent.putExtra("imageRef", roomArray.get(holder.getAdapterPosition()).getImageReference());
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
