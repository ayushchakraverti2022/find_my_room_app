package com.example.findmyroom;

import android.util.Log;

import androidx.lifecycle.HasDefaultViewModelProviderFactory;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class ViewModelData extends ViewModel {

    LiveData<String> score = new MutableLiveData<String>("This is ayush");

    public void setScore(LiveData<String> score) {
        this.score = score;
    }
}
