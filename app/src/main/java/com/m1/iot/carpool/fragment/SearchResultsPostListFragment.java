package com.m1.iot.carpool.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.m1.iot.carpool.PreviewCarpoolDetailActivity;
import com.m1.iot.carpool.R;
import com.m1.iot.carpool.models.Carpool;
import com.m1.iot.carpool.models.Post;
import com.m1.iot.carpool.viewholder.OnItemClickListener;
import com.m1.iot.carpool.viewholder.PotentialCarpoolListRecyclerAdapter;

import java.util.ArrayList;

public class SearchResultsPostListFragment extends Fragment implements OnItemClickListener {

    private static final String TAG = "SearchResultsPostListFragment";
    public static final String EXTRA_SEARCH_RESULTS = "SearchResultsPostListFragment.searchResults";
    public static final String EXTRA_POTENTIAL_CARPOOL_RESULTS = "SearchResultsPostListFragment.potentialCarpoolResults";

    private RecyclerView mRecycler;
    private PotentialCarpoolListRecyclerAdapter mAdapter;
    private LinearLayoutManager mManager;

    private ArrayList<Post> mPostList;
    private ArrayList<Carpool> mPotentialCarpools;

    public SearchResultsPostListFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Get list of Post objects to display:
        mPostList = getArguments().getParcelableArrayList(SearchResultsPostListFragment.EXTRA_SEARCH_RESULTS);

        if (mPostList == null) {
            throw new IllegalArgumentException(TAG + ": Must pass EXTRA_SEARCH_RESULTS.");
        }

        mPotentialCarpools = getArguments().getParcelableArrayList(SearchResultsPostListFragment.EXTRA_POTENTIAL_CARPOOL_RESULTS);

        if (mPotentialCarpools == null) {
            throw new IllegalArgumentException(TAG + ": Must pass EXTRA_POTENTIAL_CARPOOL_RESULTS.");
        }

        View rootView;

        rootView = inflater.inflate(R.layout.fragment_all_posts, container, false);



        if (mPotentialCarpools.size() == 0) {
            System.out.println("No search results found");
            rootView = inflater.inflate(R.layout.no_result_found, container, false);
            TextView noResultsFoundText = rootView.findViewById(R.id.no_posts_found_text_2);
        }
        else {
            mRecycler = rootView.findViewById(R.id.messages_list);

            mRecycler.setHasFixedSize(true);
        }


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        System.out.println("in Fragment onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        //postType = savedInstanceState.getBundle("postType");

        if (mPotentialCarpools.size() > 0) {
            // Set up Layout Manager, reverse layout
            mManager = new LinearLayoutManager(getActivity());
            mManager.setReverseLayout(true);
            mManager.setStackFromEnd(true);
            mRecycler.setLayoutManager(mManager);

            loadPosts();
        }



    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (mAdapter != null) {
//            // mAdapter.cleanup(); // TODO?
//        }
    }


    private void loadPosts() {
        System.out.println("About to load posts.....");

        // With Potential Carpool objects:
        mAdapter = new PotentialCarpoolListRecyclerAdapter(mPotentialCarpools);

        mRecycler.setAdapter(mAdapter);

        mAdapter.setClickListener(this);
    }

    @Override
    public void onClick(View view, int position) {
        // Get potential carpool post clicked from position in list:

        Carpool potentialCarpoolClicked = mPotentialCarpools.get(position);

        System.out.println("potentialCarpoolClicked = " + potentialCarpoolClicked);

        // Launch PreviewCarpoolDetailActivity
        Intent intent = new Intent(getActivity(), PreviewCarpoolDetailActivity.class);

        intent.putExtra(PreviewCarpoolDetailActivity.EXTRA_CARPOOL_OBJECT, potentialCarpoolClicked);

        startActivity(intent);
    }
}
