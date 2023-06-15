package com.example.findmyroom.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.appbroker.roundedimageview.RoundedImageView;
import com.example.findmyroom.ImageViewActivity;
import com.example.findmyroom.MainActivity;
import com.example.findmyroom.Models.MainActivityModel;
import com.example.findmyroom.R;
import com.example.findmyroom.ViewModelFolder.MainActivityViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.regex.Pattern;

public class MainActivityAdapter extends  RecyclerView.Adapter<MainActivityAdapter.viewHolder>{
    Context context;
    String loginType;
    public boolean checkNetwork(){

        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);

        return  connected;
    };
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    ArrayList<MainActivityModel> roomArray = new ArrayList<MainActivityModel>();
    MainActivityModel mainActivityModel = new MainActivityModel();

    public MainActivityAdapter(Context context, ArrayList<MainActivityModel> roomArray,String loginType) {
        this.context = context;
        this.roomArray = roomArray;
        this.loginType = loginType;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.room_cardview,parent,false);
        return new viewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
              mainActivityModel = roomArray.get(position);

              Picasso.get().load(mainActivityModel.getCoverimage()).into(holder.coverimage);

              holder.address.setText(mainActivityModel.getAddress());
              holder.phonenumber.setText(mainActivityModel.getPhonenumber().substring(0,5));
              holder.price.setText(mainActivityModel.getPrice());
              holder.rentername.setText(mainActivityModel.getRentername());

              //phone call
              holder.phonenumber.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + roomArray.get(position).getPhonenumber()));
                      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                      context.startActivity(intent);
                  }
              });

              // like
              holder.like.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {


                              DatabaseReference reference = roomArray.get(position).getRoomReference();
                              if(Pattern.matches(loginType,"PG")){
                                  if(checkNetwork()){

                                  holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_star__24, 0, 0, 0);

                                  ArrayList<String > images = roomArray.get(position).getImageReference();
                                  for(int i =0; i<images.size();i++){
                                      firebaseDatabase.getReference().child("PG").child(firebaseAuth.getUid()).child("LikedRooms").child(roomArray.get(position).getRoomReference().getKey().toString()).child("RoomImage").push().setValue(images.get(i));
                                  }

                                  firebaseDatabase.getReference().child("PG").child(firebaseAuth.getUid()).child("LikedRooms").child(roomArray.get(position).getRoomReference().getKey().toString()).child("Price").setValue(mainActivityModel.getPrice());
                                  firebaseDatabase.getReference().child("PG").child(firebaseAuth.getUid()).child("LikedRooms").child(roomArray.get(position).getRoomReference().getKey().toString()).child("Address").setValue(mainActivityModel.getAddress());
                                  firebaseDatabase.getReference().child("PG").child(firebaseAuth.getUid()).child("LikedRooms").child(roomArray.get(position).getRoomReference().getKey().toString()).child("Renter").setValue(mainActivityModel.getRentername());
                                  firebaseDatabase.getReference().child("PG").child(firebaseAuth.getUid()).child("LikedRooms").child(roomArray.get(position).getRoomReference().getKey().toString()).child("Contact").setValue(mainActivityModel.getPhonenumber()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                      @Override
                                      public void onSuccess(Void unused) {
                                          Toast.makeText(context, "Added to your Liked Rooms", Toast.LENGTH_SHORT).show();
                                      }
                                  });;
                                  Toast.makeText(context, "Wait for 3 seconds", Toast.LENGTH_SHORT).show();
                              }else{
                                      Toast.makeText(context, "Check Internet Connectivity", Toast.LENGTH_SHORT).show();
                                  }
                              }else{
                                  Toast.makeText(context, "Login as PG to add liked rooms", Toast.LENGTH_SHORT).show();
                              }

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
