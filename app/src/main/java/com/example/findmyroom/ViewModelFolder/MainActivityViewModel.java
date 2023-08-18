package com.example.findmyroom.ViewModelFolder;

import android.os.ParcelUuid;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.findmyroom.Models.MainActivityModel;

import java.security.PublicKey;
import java.util.ArrayList;

public class MainActivityViewModel extends ViewModel {
      public ArrayList<MainActivityModel> roomArrayList = new ArrayList<MainActivityModel>();
     public MutableLiveData<Long> roomArrayLiveData = new MutableLiveData<Long>();
    public ArrayList<MainActivityModel> roomArraySearchList = new ArrayList<MainActivityModel>();

}
