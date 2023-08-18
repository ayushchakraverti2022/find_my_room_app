package com.example.findmyroom.ViewModelFolder;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ImageviewViewModel extends ViewModel {
    public ArrayList<String> images = new ArrayList<>();
    public MutableLiveData<ArrayList<String>> signal = new MutableLiveData<>();

}
