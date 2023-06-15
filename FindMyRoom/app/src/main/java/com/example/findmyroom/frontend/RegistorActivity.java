package com.example.findmyroom.frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.findmyroom.databinding.ActivityRegistorBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegistorActivity extends AppCompatActivity {
    ActivityRegistorBinding activityRegistorBinding;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    // network check
    public boolean checkNetwork(){

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);

        return  connected;
    };
    // network check

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRegistorBinding = ActivityRegistorBinding.inflate(getLayoutInflater());

        setContentView(activityRegistorBinding.getRoot());
       // setting default username and password
        String usremail = getIntent().getExtras().getString("useremail");
        String pssword =  getIntent().getExtras().getString("password");
        activityRegistorBinding.registerusremail.setText(usremail);
        activityRegistorBinding.registorpsswrd.setText(pssword);
        //ends


        //progress dialog

        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Processing");
        pd.setMessage("Please wait...");
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        //ends

        //shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        boolean pg = sharedPreferences.getBoolean("PG", false);
        boolean renter = sharedPreferences.getBoolean("Renter", false);
       // shared preference ends
        // pg registeration
        activityRegistorBinding.pgregistorbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!activityRegistorBinding.registerusremail.getText().toString().isEmpty() && !activityRegistorBinding.registorpsswrd.getText().toString().isEmpty()) {
                    if(checkNetwork()==true){
                        pd.show();
                firebaseAuth.createUserWithEmailAndPassword(activityRegistorBinding.registerusremail.getText().toString(), activityRegistorBinding.registorpsswrd.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            String username = activityRegistorBinding.registerusremail.getText().toString();
                            firebaseDatabase.getReference().child("PG").child(firebaseAuth.getUid()).child("username").setValue(username).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    firebaseDatabase.getReference().child("PG").child(firebaseAuth.getUid()).child("LikedRooms").setValue("kk");
                                    pd.dismiss();
                                    Toast.makeText(RegistorActivity.this, "User created", Toast.LENGTH_SHORT).show();
                                    // choosing pg true
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("PG", true);
                                   editor.apply();
                                    // ends
                                    onBackPressed();
                                }
                            });


                        }else{
                            pd.dismiss();
                            Toast.makeText(RegistorActivity.this, "Error \n Try later", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }

                    }
                });
                    }else{
                        pd.dismiss();
                        Toast.makeText(RegistorActivity.this, "Check Internet Connectivity", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(RegistorActivity.this, "Please fill the entries", Toast.LENGTH_SHORT).show();
                }

            }
        });
   /// pg registration ends


        /// renter registration

        activityRegistorBinding.renterregisterbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!activityRegistorBinding.registerusremail.getText().toString().isEmpty() && !activityRegistorBinding.registorpsswrd.getText().toString().isEmpty()) {
                    if(checkNetwork()==true){
                        pd.show();

                firebaseAuth.createUserWithEmailAndPassword(activityRegistorBinding.registerusremail.getText().toString(), activityRegistorBinding.registorpsswrd.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                            if(task.isSuccessful()){

                                String username = activityRegistorBinding.registerusremail.getText().toString();
                                firebaseDatabase.getReference().child("Renter").child(firebaseAuth.getUid()).child("username").setValue(username).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        firebaseDatabase.getReference().child("Renter").child(firebaseAuth.getUid()).child("RoomCount").setValue("0");
                                        firebaseDatabase.getReference().child("Renter").child(firebaseAuth.getUid()).child("contact").setValue("contact");
                                        Toast.makeText(RegistorActivity.this, "User created", Toast.LENGTH_SHORT).show();
                                        // choosing renter true
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putBoolean("Renter", true);
                                        editor.apply();
                                        pd.dismiss();
                                        onBackPressed();
                                        // ends



                                    }
                                });


                            }else{
                                pd.dismiss();
                                Toast.makeText(RegistorActivity.this, "Error \n Try later", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }

                    }
                });
                    }else{
                        pd.dismiss();
                        Toast.makeText(RegistorActivity.this, "Check Internet Connectivity", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(RegistorActivity.this, "Please fill the entries", Toast.LENGTH_SHORT).show();
                }

            }
        });

      // renter registration ends


    }
}