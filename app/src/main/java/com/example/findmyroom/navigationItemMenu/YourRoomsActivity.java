package com.example.findmyroom.navigationItemMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import com.example.findmyroom.MainActivity;
import com.example.findmyroom.Models.LikedRoomModel;
import com.example.findmyroom.Models.YourRoomsModel;
import com.example.findmyroom.R;
import com.example.findmyroom.ViewModelFolder.MainActivityViewModel;
import com.example.findmyroom.ViewModelFolder.YourRoomViewModel;
import com.example.findmyroom.adapters.MainActivityAdapter;
import com.example.findmyroom.adapters.YourRoomsAdapter;
import com.example.findmyroom.databinding.ActivityYourRoomsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class YourRoomsActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    YourRoomViewModel yourRoomViewModel;

    YourRoomsAdapter yourRoomsAdapter;

    ActivityYourRoomsBinding yourRoomsBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        yourRoomsBinding = ActivityYourRoomsBinding.inflate(getLayoutInflater());
        setContentView(yourRoomsBinding.getRoot());
        Toolbar toolbar = findViewById(R.id.roomtoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        yourRoomViewModel = new ViewModelProvider(this).get(YourRoomViewModel.class);

        yourRoomViewModel.yourroomLiveData.observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                //adapter
                yourRoomsAdapter=  new YourRoomsAdapter(YourRoomsActivity.this, yourRoomViewModel.yourRoomArrayList);
                yourRoomsBinding.yourRoomsRecyclerview.setLayoutManager(new LinearLayoutManager(YourRoomsActivity.this,LinearLayoutManager.VERTICAL,false));
                yourRoomsBinding.yourRoomsRecyclerview.setAdapter(yourRoomsAdapter);
                //adaper
            }
        });

        firebaseDatabase.getReference().child("Renter").child(firebaseAuth.getUid()).child("Rooms").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                yourRoomViewModel.yourRoomArrayList.clear();
                for(DataSnapshot roomid: snapshot.getChildren()){



                    String address = (String) roomid.child("State").getValue() + "\n" +(String) roomid.child("City").getValue() + "\n" +(String) roomid.child("LandMark").getValue()+ "\n" +(String) roomid.child("Locality").getValue() ;
                   // String address = (String) roomid.child("State").getValue();

                    String price = roomid.child("Price").getValue().toString();
                    String rentercontact = getIntent().getExtras().getString("contact");
                    String rentername = getIntent().getExtras().getString("username");
                    String coverimage= "";
                    for(DataSnapshot image : roomid.child("RoomImage").getChildren()){
                        coverimage = image.getValue().toString();
                        break;
                    }
                    ArrayList<String> imageRef = new ArrayList<>();

                            for(DataSnapshot image : roomid.child("RoomImage").getChildren()){
                                String i_image = image.getValue().toString();
                                imageRef.add(i_image);
                            }


                    YourRoomsModel yourRoomsModel = new YourRoomsModel(address,price,rentername,rentercontact,coverimage,imageRef,roomid.getRef());
                    yourRoomViewModel.yourRoomArrayList.add(0,yourRoomsModel);
                    yourRoomViewModel.yourroomLiveData.setValue(System.currentTimeMillis());



                }
            }else{
                    firebaseDatabase.getReference().child("Renter").child(firebaseAuth.getUid()).child("Rooms").setValue("no");
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