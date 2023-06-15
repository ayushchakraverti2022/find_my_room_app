package com.example.findmyroom.frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.example.findmyroom.MainActivity;
import com.example.findmyroom.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    ActivityLoginBinding loginBinding;


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
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(loginBinding.getRoot());






        //Intent definition to mainActivity
        Intent intent = new Intent(this, MainActivity.class);
        //knowing usrtype

        // sharepreferences
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        boolean value = sharedPreferences.getBoolean("flag", false);
        boolean pg = sharedPreferences.getBoolean("PG", false);
        boolean renter = sharedPreferences.getBoolean("Renter", false);

        // end with sharedpreferences getting value of flag

        // if already login here
        if (value == true && pg == true) {
            intent.putExtra("logintype", "PG");
            startActivity(intent);
            finish();
        } else if (value == true && renter == true) {
            intent.putExtra("logintype", "Renter");
            startActivity(intent);
            finish();
        }
        //sent user to their default Activity
        //end

        //progress dialog
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Processing");
        pd.setCancelable(false);
        pd.setMessage("Please wait...");
        pd.setCanceledOnTouchOutside(false);
        //ends


        // login button
        loginBinding.loginbtn.setOnClickListener(new View.OnClickListener() {

               @Override
               public void onClick (View
               view){
               SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
               boolean flag = sharedPreferences.getBoolean("flag", false);
               if (!loginBinding.loginusremail.getText().toString().isEmpty() && !loginBinding.loginpsswrd.getText().toString().isEmpty() ) {
                   if (checkNetwork()==true){

                   pd.show();
                       SharedPreferences.Editor editor = sharedPreferences.edit();
                       boolean pg = sharedPreferences.getBoolean("PG", false);
                       boolean renter = sharedPreferences.getBoolean("Renter", false);
                   firebaseAuth.signInWithEmailAndPassword(loginBinding.loginusremail.getText().toString(), loginBinding.loginpsswrd.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           if (task.isSuccessful()) {

                               if (!flag) {
                                   editor.putBoolean("flag", true);
                                   editor.apply();

                                   }
                               if(pg) {

                                   intent.putExtra("logintype", "PG");
                                   pd.dismiss();
                                   startActivity(intent);
                                   finish();
                               } else if (renter) {

                                   intent.putExtra("logintype", "Renter");
                                   pd.dismiss();
                                   startActivity(intent);
                                   finish();
                               }else{

                                   new Handler().post(new Runnable() {
                                       @Override
                                       public void run() {
                                           firebaseDatabase.getReference().child("PG").addListenerForSingleValueEvent(new ValueEventListener() {
                                               @Override
                                               public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                   for(DataSnapshot snapshot1:snapshot.getChildren()){
                                                       if(Pattern.matches(firebaseAuth.getUid(),snapshot1.getKey())){
                                                           SharedPreferences sharedPreferences = getSharedPreferences("login",MODE_PRIVATE);
                                                           SharedPreferences.Editor editor = sharedPreferences.edit();
                                                           boolean pg = sharedPreferences.getBoolean("PG", false);
                                                           boolean renter = sharedPreferences.getBoolean("Renter", false);
                                                           editor.putBoolean("PG", true);
                                                           editor.apply();

                                                           intent.putExtra("logintype", "PG");
                                                           pd.dismiss();
                                                           startActivity(intent);
                                                           finish();

                                                           break;
                                                       }
                                                   }
                                               }

                                               @Override
                                               public void onCancelled(@NonNull DatabaseError error) {

                                               }
                                           });
                                       }
                                   });
                                    new Handler().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            firebaseDatabase.getReference().child("Renter").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                                                        if(Pattern.matches(firebaseAuth.getUid(),snapshot1.getKey())){
                                                            SharedPreferences sharedPreferences = getSharedPreferences("login",MODE_PRIVATE);
                                                            boolean pg = sharedPreferences.getBoolean("PG", false);
                                                            boolean renter = sharedPreferences.getBoolean("Renter", false);

                                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                                            editor.putBoolean("Renter", true);
                                                            editor.apply();

                                                            intent.putExtra("logintype", "Renter");
                                                            pd.dismiss();
                                                            startActivity(intent);
                                                            finish();
                                                            break;
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    });
                               }

                           }else {
                               pd.dismiss();
                                      Toast.makeText(LoginActivity.this, "Incorrect entries \n Please register ", Toast.LENGTH_SHORT).show();
                              }

                       }
                   });
                   }else{
                       Toast.makeText(LoginActivity.this, "Check Internet Connectivity", Toast.LENGTH_SHORT).show();
                   }
               } else {
                   Toast.makeText(LoginActivity.this, "Please fill the entries", Toast.LENGTH_SHORT).show();
               }
           }
           }

        );

  // login button ends







        loginBinding.registorbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=  new Intent(LoginActivity.this, RegistorActivity.class);
                intent1.putExtra("useremail",loginBinding.loginusremail.getText().toString() );
                intent1.putExtra("password", loginBinding.loginpsswrd.getText().toString());
                startActivity(intent1);
            }
        });



    }
}