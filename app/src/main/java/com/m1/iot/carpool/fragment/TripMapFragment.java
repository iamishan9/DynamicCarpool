package com.m1.iot.carpool.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by timhdavis on 5/18/18.
 */

public class TripMapFragment extends Fragment implements OnMapReadyCallback {
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // TODO...

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO...

    }
}
