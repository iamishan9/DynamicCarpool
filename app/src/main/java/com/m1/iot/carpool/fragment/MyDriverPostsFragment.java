package com.m1.iot.carpool.fragment;


import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyDriverPostsFragment extends MyPostsFragment {


    public MyDriverPostsFragment() {
        // Required empty public constructor
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return  databaseReference.child("user-posts").child(super.uID)
                .child("driveOffers");
    }

}
