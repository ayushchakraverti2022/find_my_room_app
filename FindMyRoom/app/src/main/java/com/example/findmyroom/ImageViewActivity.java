package com.example.findmyroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.findmyroom.ViewModelFolder.ImageviewViewModel;
import com.example.findmyroom.adapters.ImageViewAdapter;
import com.example.findmyroom.databinding.ActivityImageViewBinding;

import java.util.ArrayList;
import java.util.Objects;

public class ImageViewActivity extends AppCompatActivity {
    ImageviewViewModel imageviewViewModel;
    ImageViewAdapter imageViewAdapter;
    ActivityImageViewBinding activityImageViewBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityImageViewBinding = ActivityImageViewBinding.inflate(getLayoutInflater());
        setContentView(activityImageViewBinding.getRoot());
        Toolbar toolbar = findViewById(R.id.imagetoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        imageviewViewModel = new ViewModelProvider(this).get(ImageviewViewModel.class);


        imageviewViewModel.images = (ArrayList<String>) getIntent().getExtras().get("imageRef");
        imageViewAdapter = new ImageViewAdapter(imageviewViewModel.images,this);
        activityImageViewBinding.imageviewRecyclerview.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        activityImageViewBinding.imageviewRecyclerview.setAdapter(imageViewAdapter);













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