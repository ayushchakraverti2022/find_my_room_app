package com.example.findmyroom.Models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class MainActivityModel {
    public String getCoverimage() {
        return coverimage;
    }

    public ArrayList<String> getImageReference() {
        return imageReference;
    }

    public DatabaseReference getRoomReference() {
        return roomReference;
    }

    String address, price, rentername , phonenumber , coverimage;
    ArrayList<String> imageReference = new ArrayList<>();
    DatabaseReference  roomReference;

    public MainActivityModel(String address, String price, String rentername, String phonenumber, String coverimage, ArrayList<String> imageReference, DatabaseReference roomReference) {
        this.address = address;
        this.price = price;
        this.rentername = rentername;
        this.phonenumber = phonenumber;
        this.coverimage = coverimage;
        this.imageReference = imageReference;
        this.roomReference = roomReference;
    }

    public MainActivityModel() {

    }

    public String getAddress() {
        return address;
    }

    public String getPrice() {
        return price;
    }

    public String getRentername() {
        return rentername;
    }

    public String getPhonenumber() {
        return phonenumber;
    }






}
