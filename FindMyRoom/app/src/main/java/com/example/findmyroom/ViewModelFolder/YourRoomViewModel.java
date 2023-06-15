package com.example.findmyroom.ViewModelFolder;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.findmyroom.Models.LikedRoomModel;
import com.example.findmyroom.Models.YourRoomsModel;

import java.util.ArrayList;

public class YourRoomViewModel extends ViewModel {
    public ArrayList<YourRoomsModel> yourRoomArrayList = new ArrayList<YourRoomsModel>();
    public MutableLiveData<Long> yourroomLiveData = new MutableLiveData<Long>();
}
