package com.m1.iot.carpool;

import com.m1.iot.carpool.models.Carpool;
import com.m1.iot.carpool.models.Post;

import java.util.ArrayList;

/**
 * Created by timhdavis on 4/15/18.
 */

public interface PostSearchResultsListener {
    void onSearchResultsFound(ArrayList<Post> searchResults, ArrayList<Carpool> potentialCarpools);
}
