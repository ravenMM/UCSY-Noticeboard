package com.climbdev2016.noticeboard.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Constants {

    public static final int CODE_GALLERY_REQUEST = 3;
    public static final int CODE_PROFILE_GALLERY_REQUEST = 1;
    public static final DatabaseReference FIREBASE_DATABASE_REFERENCE = FirebaseDatabase.getInstance().getReference();
    public static final String SHARE_HASH_TAG = "#Noticeboard";


}
