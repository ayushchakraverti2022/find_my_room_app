package com.example.findmyroom.navigationItemMenu;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.findmyroom.MainActivity;
import com.example.findmyroom.Models.LikedRoomModel;
import com.example.findmyroom.Models.MainActivityModel;
import com.example.findmyroom.R;
import com.example.findmyroom.ViewModelFolder.LikedRoomsViewModel;
import com.example.findmyroom.adapters.LikeRoomAdapter;
import com.example.findmyroom.adapters.MainActivityAdapter;
import com.example.findmyroom.databinding.ActivityLikedRoomsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class LikedRoomsActivity extends AppCompatActivity {

    LikedRoomsViewModel likedRoomsViewModel;
    LikeRoomAdapter likeRoomAdapter;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();


    ActivityLikedRoomsBinding likedRoomsBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        likedRoomsBinding= ActivityLikedRoomsBinding.inflate(getLayoutInflater());
        setContentView(likedRoomsBinding.getRoot());
         setSupportActionBar(likedRoomsBinding.liketoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        likedRoomsViewModel = new ViewModelProvider(this).get(LikedRoomsViewModel.class);
        likedRoomsViewModel.likedroomLiveData.observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                likeRoomAdapter=  new LikeRoomAdapter(getApplicationContext(),likedRoomsViewModel.likeroomArrayList);
                likedRoomsBinding.likerecylerview.setLayoutManager(new LinearLayoutManager(LikedRoomsActivity.this,LinearLayoutManager.VERTICAL,false));
                likedRoomsBinding.likerecylerview.setAdapter(likeRoomAdapter);


            }
        });
        firebaseDatabase.getReference().child("PG").child(firebaseAuth.getUid()).child("LikedRooms").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){


                likedRoomsViewModel.likeroomArrayList.clear();
                for(DataSnapshot roomid: snapshot.getChildren()){
                    if(roomid.exists()){

                    String address =roomid.child("Address").getValue().toString() ;
                    String price = roomid.child("Price").getValue().toString();
                    String rentercontact = roomid.child("Contact").getValue().toString();
                    String rentername = roomid.child("Renter").getValue().toString();
                    String coverimage= "";
                    for(DataSnapshot image : roomid.child("RoomImage").getChildren()){
                        coverimage = image.getValue().toString();
                        break;
                    }
                    ArrayList<String> imageRef = new ArrayList<>();

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            for(DataSnapshot image : roomid.child("RoomImage").getChildren()){
                                String i_image = image.getValue().toString();
                                imageRef.add(i_image);
                            }

                        }
                    });

                    LikedRoomModel likedRoomModel = new LikedRoomModel(address,price,rentername,rentercontact,coverimage,imageRef,roomid.getRef());
                    likedRoomsViewModel.likeroomArrayList.add(0,likedRoomModel);
                    likedRoomsViewModel.likedroomLiveData.setValue(System.currentTimeMillis());}
                    else{
                        firebaseDatabase.getReference().child("PG").child(firebaseAuth.getUid()).child("LikedRooms").setValue("no");

                    }

                }}
                else{
                    firebaseDatabase.getReference().child("PG").child(firebaseAuth.getUid()).child("LikedRooms").setValue("no");
                    onBackPressed();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()== android.R.id.home){
            onBackPressed();

        }
        return super.onOptionsItemSelected(item);
    }

}