package com.accordhk.SnapNEat.fragments;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.accordhk.SnapNEat.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GMapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GMapFragment extends BaseFragment {
    private static String LOGGER_TAG = "GMapFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String TITLE = "title";
    public static final String NAME = "name";
    public static final String RESTAURANT_ID = "restaurantId";

    // TODO: Rename and change types of parameters
    private GoogleMap mMap;
    private Bundle mBundle;
    private Double latitude, longitude;
    private String title, name;
    private int restaurantId;

    /* GPS Constant Permission */
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private SupportMapFragment mMapFragment;

    private ViewGroup infoWindow;

    private OnFragmentInteractionListener mListener;

    public GMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param latitude Parameter 1.
     * @param longitude Parameter 2.
     * @return A new instance of fragment GMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GMapFragment newInstance(float latitude, float longitude, String title, String name) {
        GMapFragment fragment = new GMapFragment();
        Bundle args = new Bundle();
        args.putFloat(LATITUDE, latitude);
        args.putFloat(LONGITUDE, longitude);
        args.putString(TITLE, title);
        args.putString(NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBundle = savedInstanceState;
        if (getArguments() != null) {
            latitude = (double) getArguments().getFloat(LATITUDE);
            longitude = (double) getArguments().getFloat(LONGITUDE);
            title = (String) getArguments().getString(TITLE);
            name = (String) getArguments().getString(NAME);
            restaurantId = (int) getArguments().getInt(RESTAURANT_ID);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        infoWindow = (ViewGroup) inflater.inflate(R.layout.map_infowindow, null);

        TextView action_bar_title = (TextView) view.findViewById(R.id.title);
        action_bar_title.setText(title);

        ImageButton btn_back = (ImageButton) view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.goBack();
                }
            }
        });

        ImageButton btn_location = (ImageButton) view.findViewById(R.id.btn_location);
        btn_location.setVisibility(View.INVISIBLE);

        if (view.findViewById(R.id.map) != null) {

//            FragmentTransaction mTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//            mMapFragment = SupportMapFragment.newInstance();
//            mTransaction.add(R.id.map, mMapFragment);
//            mTransaction.commit();
            mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

            mMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;

                    LatLng userPosition = new LatLng(latitude, longitude);

                    final TextView content = (TextView) infoWindow.findViewById(R.id.content);
                    ImageView iv_plus = (ImageView) infoWindow.findViewById(R.id.iv_plus);
                    iv_plus.setVisibility(View.GONE);
                    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                        @Override
                        public View getInfoWindow(Marker marker) {
                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {
                            if(marker.getTitle() != null && !marker.getTitle().isEmpty()) {
                                content.setText(marker.getTitle());
                                return infoWindow;
                            }
                            return null;
                        }
                    });

                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
//                                                Toast.makeText(mContext, marker.getTitle()+"Info window clicked",
//                                                        Toast.LENGTH_SHORT).show();

                            try {
                                final Marker mark = marker;
                                new Handler().postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        try {
                                            if(mListener != null)
                                                mListener.showRestaurantDetails(restaurantId);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, 100);

                                return;
                            } catch (Exception e) {
                                Log.e("Exception", "Exception :: " + e.getMessage());
                            }
                        }
                    });

                    mMap.addMarker(new MarkerOptions().position(userPosition).title(name));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 10));

                }
            });
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void goBack();
        void showRestaurantDetails(int restaurantId);
    }

    private void setUpMapIfNeeded(View inflatedView) {
        if (mMap == null) {
            mMap = ((MapView) inflatedView.findViewById(R.id.map)).getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        Log.d(LOGGER_TAG, "latitude: "+String.valueOf(latitude)+" longitude: "+String.valueOf(longitude));
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Marker"));
    }

//    @Override
//    public void onResume() {
//        mGoogleApiClient.connect();
//        super.onResume();
////        mMapView.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        mGoogleApiClient.disconnect();
//        super.onPause();
////        mMapView.onPause();
//    }
//
//    @Override
//    public void onDestroy() {
////        mMapView.onDestroy();
//        mGoogleApiClient.disconnect();
//        super.onDestroy();
//    }
}
