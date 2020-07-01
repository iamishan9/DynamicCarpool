/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.m1.iot.carpool;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.m1.iot.carpool.models.Post;
import com.m1.iot.carpool.models.User;

import java.util.HashMap;
import java.util.Map;

public class  MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private ImageButton mSignOut;
    private User user;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_0_main);
        this.getUser(getUid());
        System.out.println(getUid());
        mSignOut = (ImageButton) this.findViewById(R.id.profile_page_sign_out);
        mSignOut.setVisibility(View.GONE);
        if(this.user == null){
            // mSignOut.setVisibility(View.VISIBLE);
            mSignOut.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this, SignInActivity.class));
                    finish();
                }
            });
        }

        System.out.println("About to start service.");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        findViewById(R.id.driver_mode_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, MainSubcategoryActivity.class);
                intent.putExtra(MainSubcategoryActivity.EXTRA_POST_TYPE_TO_SHOW, Post.PostType.RIDER.name());
                startActivity(intent);
            }
        });

        // "I am a passenger" -> Show me Drive Offers (including Carpools):
        findViewById(R.id.rider_mode_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, MainSubcategoryActivity.class);
                intent.putExtra(MainSubcategoryActivity.EXTRA_POST_TYPE_TO_SHOW, Post.PostType.DRIVER.name());
                startActivity(intent);
            }
        });
        pushTokenToFirebase();
        setNavBar(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return true;
        } else if (i == R.id.action_show_organizations) {
            startActivity(new Intent(this, ShowOrganizationsActivity.class));
            finish();
            return true;
        } else if (i == R.id.edit_profile){
            startActivity(new Intent(this, AddUserInformation.class));
            finish();
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void getUser(String uid) {

        // System.out.println("Starting to set user....");
        try {
            mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Need to get the user object before loading posts because the query to find posts requires user.

                    // Get User object and use the values to update the UI
                    user = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        catch (NullPointerException e){
            System.out.println("Null user");
            this.user = null;
        }
        //getUid()
    }

    //This method will push this Firebasetoken online so that
    //  the cloud functions may use it.
    public void pushTokenToFirebase(){
        Map<String, Object> childUpdates = new HashMap<>();
        String instance_id = FirebaseInstanceId.getInstance().getId();
        String instance_token = FirebaseInstanceId.getInstance().getToken();

        System.out.println(getUid());
        System.out.println(FirebaseInstanceId.getInstance().getId());
        System.out.println(instance_token);

        childUpdates.put("/users/"+getUid()+"/firebase_instance_id/", instance_id);
        childUpdates.put("/users/"+getUid()+"/firebase_instance_token/", instance_token);
        mDatabase.updateChildren(childUpdates);
    }

}
