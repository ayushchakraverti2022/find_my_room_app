package com.example.findmyroom.navigationItemMenu;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.example.findmyroom.R;
import com.example.findmyroom.ViewModelFolder.AddNewRoomViewModel;
import com.example.findmyroom.adapters.AddnewRoomAdapter;
import com.example.findmyroom.databinding.ActivityAddNewRoomBinding;
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


public class AddNewRoomActivity extends AppCompatActivity {
    ActivityAddNewRoomBinding addNewRoomBinding;
    AddNewRoomViewModel addNewRoomViewModel;
    int roomcout;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    String username, contact,logintype;


     // upload btn function

    // upload btn function


    // check network
    public boolean checkNetwork(){

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);

        return  connected;
    };
    //check network ends

    ActivityResultLauncher<String > launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addNewRoomBinding = ActivityAddNewRoomBinding.inflate(getLayoutInflater());
        setContentView(addNewRoomBinding.getRoot());
        Toolbar toolbar = findViewById(R.id.renttoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //getting intent values
        username = getIntent().getExtras().getString("username");
        contact = getIntent().getExtras().getString("contact");
        logintype = getIntent().getExtras().getString("logintype");

        //getting intent values



        //progress dialog
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Processing");
        pd.setCancelable(false);
        pd.setMessage("Please wait...");
        pd.setCanceledOnTouchOutside(false);
        //ends

       //viewmodel
        addNewRoomViewModel = new ViewModelProvider(this).get(AddNewRoomViewModel.class);
      // viewmodel

        //imagecomprssro]

        //imagecompress




        //room adapter
        AddnewRoomAdapter addnewRoomAdapter = new AddnewRoomAdapter(addNewRoomViewModel.imageUri,AddNewRoomActivity.this);
        addNewRoomBinding.addNewRoomRecyclerview.setLayoutManager(new LinearLayoutManager(AddNewRoomActivity.this,LinearLayoutManager.HORIZONTAL,true));
        addNewRoomBinding.addNewRoomRecyclerview.setAdapter(addnewRoomAdapter);
        // room adapter

        // image picker and setter
        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                  try{
                      Bitmap bitmap = MediaStore.Images.Media.getBitmap(AddNewRoomActivity.this.getContentResolver(),result);
                      ByteArrayOutputStream stream = new ByteArrayOutputStream();
                      bitmap.compress(Bitmap.CompressFormat.JPEG,20,stream);
                      byte[]  imagebit = stream.toByteArray();
                      addNewRoomViewModel.imageBytes.add(imagebit);
                      addNewRoomViewModel.imageUri.add(0,result);
                      addnewRoomAdapter.notifyDataSetChanged();


                  }catch (Exception e){}
            }
        });
        // image picker and setter


        //adding image function
        addNewRoomBinding.addNewRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   launcher.launch("image/*");
            }
        });// adding image function

        //remove image function

        addNewRoomBinding.removeNewRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!addNewRoomViewModel.imageUri.isEmpty()){
                    addNewRoomViewModel.imageUri.remove(0);
                    addnewRoomAdapter.notifyDataSetChanged();
                }

            }
        });

        //remove image function

        //savebutton action'
        addNewRoomBinding.roomsavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkNetwork()){
                    String price = String.valueOf(addNewRoomBinding.addprice.getText()),state = String.valueOf(addNewRoomBinding.addState.getText()),city = String.valueOf(addNewRoomBinding.addCity.getText()),landmark = String.valueOf(addNewRoomBinding.addLandmark.getText()),locality = String.valueOf(addNewRoomBinding.addLocality.getText());

                    if(!addNewRoomViewModel.imageUri.isEmpty() && !city.isEmpty()&&  !locality.isEmpty() && !landmark.isEmpty()&& !state.isEmpty()&& !price.isEmpty()){
                pd.show();
                firebaseDatabase.getReference().child("Renter").child(firebaseAuth.getUid()).child("RoomCount").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        roomcout = Integer.valueOf (snapshot.getValue().toString());
                        roomcout++;
                        String RoomCount = "RoomCount"+Integer.toString(roomcout);
                                int i ;
                                for( i =0; i<addNewRoomViewModel.imageBytes.size();i++){
                                    String time = Long.toString(System.currentTimeMillis());
                                    StorageReference storageReference =  firebaseStorage.getReference().child(firebaseAuth.getUid()).child(RoomCount).child(time);
                                    //compressing here
                                          try{
                                              int finalI = i;
                                              storageReference.putBytes(addNewRoomViewModel.imageBytes.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                              @Override
                                              public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                  storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                      @Override
                                                      public void onSuccess(Uri uri) {
                                                          firebaseDatabase.getReference().child("Renter").child(firebaseAuth.getUid()).child("Rooms").child(RoomCount).child("RoomImage").push().setValue(uri.toString());
                                                          Toast.makeText(AddNewRoomActivity.this, "Uploaded "+Integer.toString(finalI+1), Toast.LENGTH_SHORT).show();

                                                          if(finalI >=addNewRoomViewModel.imageBytes.size()-1){
                                                              pd.dismiss();
                                                              //onBackPressed();
                                                          }
                                                             }

                                                  });
                                              }
                                          });
                                          }
                                          catch (Exception e){

                                          };
                                    //compressign here
                                }

                        firebaseDatabase.getReference().child("Renter").child(firebaseAuth.getUid()).child("Rooms").child(RoomCount).child("Price").setValue(price);
                        firebaseDatabase.getReference().child("Renter").child(firebaseAuth.getUid()).child("Rooms").child(RoomCount).child("State").setValue(state);
                        firebaseDatabase.getReference().child("Renter").child(firebaseAuth.getUid()).child("Rooms").child(RoomCount).child("City").setValue(city);
                        firebaseDatabase.getReference().child("Renter").child(firebaseAuth.getUid()).child("Rooms").child(RoomCount).child("Locality").setValue(locality);
                        firebaseDatabase.getReference().child("Renter").child(firebaseAuth.getUid()).child("Rooms").child(RoomCount).child("LandMark").setValue(landmark);
                        // updating roomcount
                        firebaseDatabase.getReference().child("Renter").child(firebaseAuth.getUid()).child("RoomCount").setValue(roomcout);






                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                    }else{
                    Toast.makeText(AddNewRoomActivity.this, "Fill all the entries", Toast.LENGTH_SHORT).show();
                }


                }else{
                Toast.makeText(AddNewRoomActivity.this, "Check Internet Connectivity", Toast.LENGTH_SHORT).show();
            }



            }
        });

        //savebuttonaction





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