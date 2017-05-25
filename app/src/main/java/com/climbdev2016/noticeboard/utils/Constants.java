package com.climbdev2016.noticeboard.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Constants {

    public static final int CODE_GALLERY_REQUEST = 3;
    public static final int CODE_PROFILE_GALLERY_REQUEST = 1;

    public static final String SHARE_HASH_TAG = "#Noticeboard";

    public static final int MAIN_VIEW = 1;
    public static final int CATEGORY_VIEW = 2;
    public static final int PROFILE_VIEW = 3;

    public static final DatabaseReference FIREBASE_DB_REF =
            FirebaseDatabase.getInstance().getReference();
    public static final String CHILD_POST = "Post";
    public static final String CHILD_USER = "Users";
    public static final String CHILD_LINK = "Link";
}
