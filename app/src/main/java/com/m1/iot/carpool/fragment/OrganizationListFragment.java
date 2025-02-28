package com.m1.iot.carpool.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
//import com.sjsu.se195.uniride.PostDetailActivity; //TODO
import com.m1.iot.carpool.OrganizationDetailActivity; //changed
import com.m1.iot.carpool.ProfilePageActivity;
import com.m1.iot.carpool.R;
import com.m1.iot.carpool.models.Organization; //changed
//import com.sjsu.se195.uniride.viewholder.PostViewHolder; //TODO
import com.m1.iot.carpool.models.Post;
import com.m1.iot.carpool.viewholder.OrganizationViewHolder; //changed

public abstract class OrganizationListFragment extends Fragment {

    private static final String TAG = "OrganizationListFragment";

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<Organization, OrganizationViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private String ORGANIZATION_TAB_TITLE;
    protected String uID;
    private TextView mTabTitle;

    public OrganizationListFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_organizations, container, false);


        if(getActivity() instanceof ProfilePageActivity){
            this.uID = getArguments().getString("uID");
            String firstname = getArguments().getString("userName");
            this.ORGANIZATION_TAB_TITLE = firstname+ "'s Organizations";
        }
        else{
            this.uID = getUid(); // TODO: pass userID to keep query independent of current user
        }

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = (RecyclerView) rootView.findViewById(R.id.messages_list);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query organizationsQuery = getQuery(mDatabase);
        //Log.d(TAG, "organization query: " + organizationsQuery);
        FirebaseRecyclerOptions organizationOptions = new FirebaseRecyclerOptions.Builder<Organization>().setQuery(organizationsQuery, Organization.class).build();
        mAdapter = new FirebaseRecyclerAdapter<Organization, OrganizationViewHolder>(organizationOptions) {
            @NonNull
            @Override
            public OrganizationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            protected void onBindViewHolder(@NonNull final OrganizationViewHolder viewHolder, int position, @NonNull final Organization model) {
                //Log.d(TAG, "<1> organization model: " + model.name);//TODO: investigate why My Organization organizations are null here...

                final DatabaseReference organizationRef = getRef(position); //TODO: investigate: this is fine (viewing the item works).

                // Set click listener for the whole organization view
                final String organizationKey = organizationRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch OrganizationDetailActivity
                        Intent intent = new Intent(getActivity(), OrganizationDetailActivity.class);
                        intent.putExtra(OrganizationDetailActivity.EXTRA_ORGANIZATION_KEY, organizationKey);
                        startActivity(intent);
                    }
                });

                // Bind Organization to ViewHolder
                //Log.d(TAG, "<2> organization model: " + model.name);//TODO: investigate why My Organization organizations are null here...
                viewHolder.bindToOrganization(model);
            }

//            @Override
//            protected void populateViewHolder(final OrganizationViewHolder viewHolder, final Organization model, final int position) {
//                //Log.d(TAG, "<1> organization model: " + model.name);//TODO: investigate why My Organization organizations are null here...
//
//                final DatabaseReference organizationRef = getRef(position); //TODO: investigate: this is fine (viewing the item works).
//
//                // Set click listener for the whole organization view
//                final String organizationKey = organizationRef.getKey();
//                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        // Launch OrganizationDetailActivity
//                        Intent intent = new Intent(getActivity(), OrganizationDetailActivity.class);
//                        intent.putExtra(OrganizationDetailActivity.EXTRA_ORGANIZATION_KEY, organizationKey);
//                        startActivity(intent);
//                    }
//                });
//
//                // Bind Organization to ViewHolder
//                //Log.d(TAG, "<2> organization model: " + model.name);//TODO: investigate why My Organization organizations are null here...
//                viewHolder.bindToOrganization(model);
//            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    // [START post_stars_transaction]
    /*private void onStarClicked(DatabaseReference organizationRef) { // TODO
        organizationRef.runTransaction(new Transaction.Handler() { // TODO
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Organization org = mutableData.getValue(Organization.class); // TODO
                if (org == null) { // TODO
                    return Transaction.success(mutableData);
                }

//                if (p.stars.containsKey(getUid())) { // TODO
//                    // Unstar the post and remove self from stars
//                    p.starCount = p.starCount - 1; // TODO
//                    p.stars.remove(getUid()); // TODO
//                } else {
//                    // Star the post and add self to stars
//                    p.starCount = p.starCount + 1; // TODO
//                    p.stars.put(getUid(), true); // TODO
//                }

                // Set value and report transaction success
                mutableData.setValue(org); // TODO
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "orgTransaction:onComplete:" + databaseError); // TODO
            }
        });
    }
    // [END post_stars_transaction]
    */

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (mAdapter != null) {
//            mAdapter.cleanup();
//        }
//    }
    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

}
