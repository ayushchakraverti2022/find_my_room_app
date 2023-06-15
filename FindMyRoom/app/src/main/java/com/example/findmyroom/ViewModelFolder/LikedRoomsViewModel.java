package com.example.findmyroom.ViewModelFolder;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.findmyroom.Models.LikedRoomModel;
import com.example.findmyroom.Models.MainActivityModel;

import java.util.ArrayList;

public class LikedRoomsViewModel extends ViewModel {
    public ArrayList<LikedRoomModel> likeroomArrayList = new ArrayList<LikedRoomModel>();
    public MutableLiveData<Long> likedroomLiveData = new MutableLiveData<Long>();
}
