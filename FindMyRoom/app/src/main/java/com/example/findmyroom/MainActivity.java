package com.example.findmyroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findmyroom.Models.MainActivityModel;
import com.example.findmyroom.ViewModelFolder.MainActivityViewModel;
import com.example.findmyroom.adapters.MainActivityAdapter;
import com.example.findmyroom.databinding.ActivityMainBinding;
import com.example.findmyroom.frontend.SplashActivity;
import com.example.findmyroom.navigationItemMenu.AddNewRoomActivity;
import com.example.findmyroom.navigationItemMenu.LikedRoomsActivity;
import com.example.findmyroom.navigationItemMenu.YourRoomsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    MainActivityAdapter mainActivityAdapter;
     MainActivityViewModel mainViewModel;
    ActionBarDrawerToggle actionBarDrawerToggle;
    SearchView searchView;
    String usertype ;
    String contact;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    String username ;
    ActivityMainBinding mainBinding;


   // check network
    public boolean checkNetwork(){

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);

        return  connected;
    };
    //check network ends


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        //most important thing
        usertype = getIntent().getExtras().getString("logintype");
        //most important thing ends

        // separating LoginType users

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if(Pattern.matches(usertype,"Renter")){
                    mainBinding.navigationview.getMenu().findItem(R.id.liked_rooms).setVisible(false);
                }
                if(Pattern.matches(usertype,"PG")){
                    mainBinding.navigationview.getMenu().findItem(R.id.your_rooms).setVisible(false);
                    mainBinding.navigationview.getMenu().findItem(R.id.add_rooms).setVisible(false);
                    mainBinding.navigationview.getMenu().findItem(R.id.contact).setVisible(false);
                }
            }
        });
        //separating logintype users ends


        // all front end of main page
        Toolbar toolbar = findViewById(R.id.toolbar);
        recyclerView=  findViewById(R.id.mainrecylerview);
        searchView = findViewById(R.id.mainsearchview);
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,mainBinding.drawer,toolbar,R.string.app_name,R.string.app_name);
        mainBinding.drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        // all front end of main page

        //Mainviewmodel
        mainViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        mainViewModel.roomArrayLiveData.observe(MainActivity.this, new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {

                //adapter
                mainActivityAdapter=  new MainActivityAdapter(getApplicationContext(), mainViewModel.roomArrayList,usertype);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false));
                recyclerView.setAdapter(mainActivityAdapter);
                //adaper
            }
        });


        //Mainviewmodel
        //

         // searchviewitems
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mainViewModel.roomArraySearchList.clear();

                if(query.length()>0){
                    for(int i =0; i<mainViewModel.roomArrayList.size();i++){
                        if(mainViewModel.roomArrayList.get(i).getAddress().toUpperCase().contains(query.toUpperCase())){
                            MainActivityModel mainActivityModel = new MainActivityModel(mainViewModel.roomArrayList.get(i).getAddress(),mainViewModel.roomArrayList.get(i).getPrice(),mainViewModel.roomArrayList.get(i).getRentername(),mainViewModel.roomArrayList.get(i).getPhonenumber(),mainViewModel.roomArrayList.get(i).getCoverimage(),mainViewModel.roomArrayList.get(i).getImageReference(),mainViewModel.roomArrayList.get(i).getRoomReference());
                            mainViewModel.roomArraySearchList.add(mainActivityModel);
                        }
                    }
                    mainActivityAdapter=  new MainActivityAdapter(getApplicationContext(), mainViewModel.roomArraySearchList,usertype);
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false));
                    recyclerView.setAdapter(mainActivityAdapter);


                }else{
                    mainActivityAdapter=  new MainActivityAdapter(getApplicationContext(), mainViewModel.roomArrayList,usertype);
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false));
                    recyclerView.setAdapter(mainActivityAdapter);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mainViewModel.roomArraySearchList.clear();

                if(newText.length()>0){
                    for(int i =0; i<mainViewModel.roomArrayList.size();i++){
                        if(mainViewModel.roomArrayList.get(i).getAddress().toUpperCase().contains(newText.toUpperCase())){
                            MainActivityModel mainActivityModel = new MainActivityModel(mainViewModel.roomArrayList.get(i).getAddress(),mainViewModel.roomArrayList.get(i).getPrice(),mainViewModel.roomArrayList.get(i).getRentername(),mainViewModel.roomArrayList.get(i).getPhonenumber(),mainViewModel.roomArrayList.get(i).getCoverimage(),mainViewModel.roomArrayList.get(i).getImageReference(),mainViewModel.roomArrayList.get(i).getRoomReference());
                            mainViewModel.roomArraySearchList.add(mainActivityModel);
                        }
                    }
                    mainActivityAdapter=  new MainActivityAdapter(getApplicationContext(), mainViewModel.roomArraySearchList,usertype);
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false));
                    recyclerView.setAdapter(mainActivityAdapter);


                }else{
                    mainActivityAdapter=  new MainActivityAdapter(getApplicationContext(), mainViewModel.roomArrayList,usertype);
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false));
                    recyclerView.setAdapter(mainActivityAdapter);
                }

                return false;
            }
        });
        //searchveiw ends





        //progress dialog
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Processing");
        pd.setCancelable(false);
        pd.setMessage("Please wait...");
        pd.setCanceledOnTouchOutside(false);
        //ends


        //getting data from database

        new Handler().post(new Runnable() {
            @Override
            public void run() {

                firebaseDatabase.getReference().child("Renter").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mainViewModel.roomArrayList.clear();
                        for(DataSnapshot userUID: snapshot.getChildren()){
                            int roomcountvalue = Integer.parseInt(userUID.child("RoomCount").getValue().toString());
                            if(roomcountvalue>0){

                                for(int i =1;i<=roomcountvalue;i++){
                                    int finalI = i;
                                    new Handler().post(new Runnable() {
                                        @Override
                                        public void run() {


                                            String RoomNode = "RoomCount"+Integer.toString(finalI);
                                            userUID.getRef().child("Rooms").child(RoomNode).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(snapshot.exists()){
                                                    String address =(String) snapshot.child("State").getValue() + "\n" +(String)snapshot.child("City").getValue() + "\n" +(String)snapshot.child("LandMark").getValue() + "\n" +(String)snapshot.child("Locality").getValue() ;
                                                    String price = (String)snapshot.child("Price").getValue();
                                                    String rentercontact = (String)userUID.child("contact").getValue();
                                                    String rentername =(String) userUID.child("username").getValue();
                                                    String coverimage= "";
                                                    for(DataSnapshot image : snapshot.child("RoomImage").getChildren()){
                                                        coverimage = (String)image.getValue();
                                                        break;
                                                    }
                                                    ArrayList<String> imageRef = new ArrayList<>();

                                                    new Handler().post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            for(DataSnapshot image : snapshot.child("RoomImage").getChildren()){
                                                                String i_image = (String)image.getValue();
                                                                imageRef.add(i_image);
                                                            }

                                                        }
                                                    });

                                                    MainActivityModel mainActivityModel = new MainActivityModel(address,price,rentername,rentercontact,coverimage,imageRef,userUID.child(RoomNode).getRef());
                                                    mainViewModel.roomArrayList.add(0,mainActivityModel);
                                                    mainViewModel.roomArrayLiveData.setValue(System.currentTimeMillis());
                                                }}

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });



                                        }
                                    });

                                }
                            }


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        //getting data from database


        //setting usrname

       new Handler().post(new Runnable() {
           @Override
           public void run() {
               firebaseDatabase.getReference().child(usertype).child(firebaseAuth.getUid()).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       TextView textView = mainBinding.navigationview.getHeaderView(0).findViewById(R.id.viewusername);
                       String name =(String)snapshot.getValue();
                       String name1[] = name.split("\\.");
                       textView.setText(name1[0]);
                       username = name1[0];
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {
                   }
               });
           }
       });

        //setting usrname edns

        //setting contact
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                firebaseDatabase.getReference().child(usertype).child(firebaseAuth.getUid()).child("contact").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            mainBinding.navigationview.getMenu().findItem(R.id.contact).setTitle(snapshot.getValue().toString());
                            contact = (String) snapshot.getValue();
                        }else{

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
        //settring contact ends



        //changing username
        TextView textView = mainBinding.navigationview.getHeaderView(0).findViewById(R.id.viewusername);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkNetwork()){
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.setusername);
                dialog.setCanceledOnTouchOutside(false);
                EditText editText = dialog.findViewById(R.id.edittextsetusername);
                editText.setText(textView.getText());
                Button button = dialog.findViewById(R.id.buttonsave);
                dialog.show();
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String username = editText.getText().toString().toUpperCase().trim();
                        if(!username.isEmpty()){

                        firebaseDatabase.getReference().child(usertype).child(firebaseAuth.getUid()).child("username").setValue(username).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    pd.dismiss();
                                    textView.setText(username);
                                    Toast.makeText(MainActivity.this, "Username set", Toast.LENGTH_SHORT).show();

                                    dialog.dismiss();
                                } else {
                                    pd.dismiss();
                                    Toast.makeText(MainActivity.this, "Check Internet Connectivity ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });}
                        else {
                            dialog.dismiss();
                        }
                        ;
                    }
                });}else{
                Toast.makeText(MainActivity.this, "Check Internet Connectivity ", Toast.LENGTH_SHORT).show();

            }
            }
        });

        //


        //navigation item selected
        mainBinding.navigationview.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               if(item.getItemId()==R.id.add_rooms){

                   Intent intent= new Intent(MainActivity.this, AddNewRoomActivity.class);
                   intent.putExtra("logintype",usertype);
                   intent.putExtra("username",username);
                   intent.putExtra("contact",contact);

                   startActivity(intent);
               } else if (item.getItemId()==R.id.your_rooms ) {
                   Intent intent= new Intent(MainActivity.this, YourRoomsActivity.class);
                   intent.putExtra("logintype",usertype);
                   intent.putExtra("username",username);
                   intent.putExtra("contact",contact);
                   startActivity(intent);
               }
               else if (item.getItemId()==R.id.liked_rooms ) {
                   Intent intent= new Intent(MainActivity.this, LikedRoomsActivity.class);
                   intent.putExtra("logintype",usertype);
                   intent.putExtra("username",username);
                   intent.putExtra("contact",contact);
                   startActivity(intent);
               }
               else if (item.getItemId()==R.id.contact ) {


                   if(checkNetwork()){

                       Dialog dialog = new Dialog(MainActivity.this);
                       dialog.setContentView(R.layout.setusername);
                       dialog.setCanceledOnTouchOutside(false);
                       TextInputEditText editText;
                       editText = dialog.findViewById(R.id.edittextsetusername);
                       editText.setText(contact);
                       editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                       Button button = dialog.findViewById(R.id.buttonsave);
                       dialog.show();
                       button.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               String phoneNumber = editText.getText().toString().toUpperCase().trim();
                               if(!phoneNumber.isEmpty()){
                                   pd.show();
                                   firebaseDatabase.getReference().child(usertype).child(firebaseAuth.getUid()).child("contact").setValue(phoneNumber).addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {

                                           if (task.isSuccessful()) {
                                               pd.dismiss();
                                               mainBinding.navigationview.getMenu().findItem(R.id.contact).setTitle(phoneNumber);
                                               Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
                                               dialog.dismiss();
                                           } else {
                                               pd.dismiss();
                                               Toast.makeText(MainActivity.this, "Check Internet Connectivity ", Toast.LENGTH_SHORT).show();
                                           }
                                       }
                                   });}
                               else {
                                   dialog.dismiss();

                               }
                               ;
                           }
                       });

                   }else{
                       Toast.makeText(MainActivity.this, "Check Internet Connectivity", Toast.LENGTH_SHORT).show();
                   }



               }
               else if (item.getItemId()==R.id.logout ) {
                   if(checkNetwork()) {
                   AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                   alert.create();
                   alert.setCancelable(false);
                   alert.setIcon(getDrawable(R.drawable.logout_24));
                   alert.setMessage("Do you really want to Logout?");


                   alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {
                           pd.show();
                           SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
                           SharedPreferences.Editor editor = sharedPreferences.edit();
                           editor.putBoolean("flag",false);
                           editor.putBoolean("PG",false);
                           editor.putBoolean("Renter",false);
                           editor.apply();
                           Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                           firebaseAuth.signOut();
                           pd.dismiss();
                           Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                           startActivity(intent);
                           finish();

                       }
                   });
                   alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {

                       }
                   });

                       alert.show();
                   }else{
                       Toast.makeText(MainActivity.this, "Check Internet Connectivity", Toast.LENGTH_SHORT).show();
                   }
               }
               return true;
           }
       });
        // nvaigatoin item selected listenerik












    }

    @Override
    public void onBackPressed() {
        if(!searchView.isIconified()){
            searchView.setIconified(true);
        }else if(mainBinding.drawer.isOpen()){
            mainBinding.drawer.close();
        }else{
        super.onBackPressed();
    }}
}