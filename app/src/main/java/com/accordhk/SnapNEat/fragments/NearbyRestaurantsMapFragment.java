package com.accordhk.SnapNEat.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.models.ResponseRestaurants;
import com.accordhk.SnapNEat.models.Restaurant;
import com.accordhk.SnapNEat.services.ApiWebServices;
import com.accordhk.SnapNEat.utils.Constants;
import com.accordhk.SnapNEat.utils.CustomFontButton;
import com.accordhk.SnapNEat.utils.CustomFontTextView;
import com.accordhk.SnapNEat.utils.VolleySingleton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NearbyRestaurantsMapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NearbyRestaurantsMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NearbyRestaurantsMapFragment extends BaseFragment {

    private static String LOGGER_TAG = "RestaurantsMapFragment";

    /* GPS Constant Permission */
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 511;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 512;

    private int distance;

    private OnFragmentInteractionListener mListener;

    public NearbyRestaurantsMapFragment() {
        // Required empty public constructor
    }

    private SupportMapFragment mMapFragment;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private LocationListener mLocationListener;

    private ViewGroup infoWindow;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RestaurantsMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NearbyRestaurantsMapFragment newInstance(String param1, String param2) {
        NearbyRestaurantsMapFragment fragment = new NearbyRestaurantsMapFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        distance = 1;

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(LOGGER_TAG, "Location changed: " + String.valueOf(location.getLatitude()) + "--" + String.valueOf(location.getLongitude()));
                getRestaurant(location.getLatitude(), location.getLongitude());
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

//        View view = inflater.inflate(R.layout.fragment_restaurants_map, container, false);
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        infoWindow = (ViewGroup) inflater.inflate(R.layout.map_infowindow, null);
        final RelativeLayout rl_choose_distance = (RelativeLayout) view.findViewById(R.id.rl_choose_distance);

        TextView action_bar_title = (TextView) view.findViewById(R.id.title);
        action_bar_title.setText(mUtils.getStringResource(R.string.s13a_address));

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
        btn_location.setImageResource(R.drawable.distance2);
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideUp(rl_choose_distance);
            }
        });

        CustomFontTextView tv_distance_1km = (CustomFontTextView) rl_choose_distance.findViewById(R.id.tv_distance_1km);
        tv_distance_1km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distance = 1;
                mPopupBackStack.pop();
                slideDown(rl_choose_distance);
                getRestaurantsByLocation();
            }
        });

        CustomFontTextView tv_distance_2km = (CustomFontTextView) rl_choose_distance.findViewById(R.id.tv_distance_2km);
        tv_distance_2km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distance = 2;
                mPopupBackStack.pop();
                slideDown(rl_choose_distance);
                getRestaurantsByLocation();
            }
        });

        CustomFontTextView tv_distance_3km = (CustomFontTextView) rl_choose_distance.findViewById(R.id.tv_distance_3km);
        tv_distance_3km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distance = 3;
                mPopupBackStack.pop();
                slideDown(rl_choose_distance);
                getRestaurantsByLocation();
            }
        });

        CustomFontTextView tv_distance_4km = (CustomFontTextView) rl_choose_distance.findViewById(R.id.tv_distance_4km);
        tv_distance_4km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distance = 4;
                mPopupBackStack.pop();
                slideDown(rl_choose_distance);
                getRestaurantsByLocation();
            }
        });

        CustomFontTextView tv_distance_5km = (CustomFontTextView) rl_choose_distance.findViewById(R.id.tv_distance_5km);
        tv_distance_5km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distance = 5;
                mPopupBackStack.pop();
                slideDown(rl_choose_distance);
                getRestaurantsByLocation();
            }
        });

        CustomFontButton btn_distance_cancel = (CustomFontButton) rl_choose_distance.findViewById(R.id.btn_distance_cancel);
        btn_distance_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupBackStack.pop();
                slideDown(rl_choose_distance);
            }
        });

        if (view.findViewById(R.id.map) != null) {

            FragmentTransaction mTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            mMapFragment = SupportMapFragment.newInstance();
            mTransaction.add(R.id.map, mMapFragment);
            mTransaction.commit();

            try {

                if(mGoogleApiClient == null) {

                    mGoogleApiClient = new GoogleApiClient.Builder(getContext(), new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                            Log.d(LOGGER_TAG, "mGoogleApiClient connected!!!");
                            getRestaurantsByLocation();
                        }

                        @Override
                        public void onConnectionSuspended(int i) {

                        }
                    }, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                        }
                    }).addApi(LocationServices.API).build();
                    mGoogleApiClient.connect();

                } else {
                    getRestaurantsByLocation();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    public void getRestaurantsByLocation() {

        String provider = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(provider.isEmpty()) {
            getActivity().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Log.d(LOGGER_TAG, "Permission granted");

            if(mGoogleApiClient != null) {
                Log.d(LOGGER_TAG, "mGoogleApiClient.isConnected() ? "+mGoogleApiClient.isConnected());
                if(mGoogleApiClient.isConnected()) {

                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (mLastLocation != null) {
                        Log.d(LOGGER_TAG, "Last known location");
                        getRestaurant(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                    } else {
                        Log.d(LOGGER_TAG, "request Location updates");
                        mLocationRequest = createLocationRequest();
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationListener);
                    }

                } else
                    mGoogleApiClient.connect();
            }

        } else {

            Log.d(LOGGER_TAG, "Asking for permission");

            // The ACCESS_COARSE_LOCATION is denied, then I request it and manage the result in
            // onRequestPermissionsResult() using the constant MY_PERMISSION_ACCESS_FINE_LOCATION
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(LOGGER_TAG, "Check permission for ACCESS_COARSE_LOCATION");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_ACCESS_COARSE_LOCATION);
            }
            // The ACCESS_FINE_LOCATION is denied, then I request it and manage the result in
            // onRequestPermissionsResult() using the constant MY_PERMISSION_ACCESS_FINE_LOCATION
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(LOGGER_TAG, "Check permission for ACCESS_FINE_LOCATION");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
            }
        }

    }

    /**
     * Calls get restaurants api and displays restaurant markers in map
     * @param lat
     * @param lon
     */
    private void getRestaurant(double lat, double lon) {

        final float latitude = (float) lat;
        final float longitude = (float) lon;

        mMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                final GoogleMap mMap = googleMap;

                try {
                    Map<String, String> params = mUtils.getBaseRequestMap();
                    params.put(Constants.STR_PAGE, String.valueOf(-1));

//                    if(latitude != Constants.FAKE_LAT_LONG)
                        params.put(Restaurant.LATITUDE, String.valueOf(latitude));
//                        params.put(Restaurant.LATITUDE, String.valueOf(1.3525));
//                    if(longitude != Constants.FAKE_LAT_LONG)
                        params.put(Restaurant.LONGITUDE, String.valueOf(longitude));
//                        params.put(Restaurant.LATITUDE, String.valueOf(103.958248));
                    params.put(Restaurant.DISTANCE, String.valueOf(distance) );

                    mProgressDialog.show();
                    mApi.getRestaurants(params, mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
                        @Override
                        public void onResponse(Object object) {
                            try {
                                final ResponseRestaurants restaurants = (ResponseRestaurants) object;

                                if (restaurants != null) {
                                    if (restaurants.getStatus() != Constants.RES_SUCCESS) {
                                        mUtils.dismissDialog(mProgressDialog);
                                        mUtils.getErrorDialog(restaurants.getMessage()).show();
                                    } else {
                                        for (int i = 0; i < restaurants.getResults().size(); i++) {
                                            Restaurant res = restaurants.getResults().get(i);

                                            MarkerOptions markerOptions = new MarkerOptions();
                                            markerOptions.position(new LatLng(res.getLatitude(), res.getLongitude()))
                                                    .title(res.getName().trim());

                                            if (res.getLocation() != null)
                                                markerOptions.snippet(res.getLocation().trim());

                                            mMap.addMarker(markerOptions);
//                                            Marker marker = mMap.addMarker(markerOptions);
//                                            marker.showInfoWindow();
                                        }

                                        ImageView iv_plus = (ImageView) infoWindow.findViewById(R.id.iv_plus);
                                        iv_plus.setVisibility(View.GONE);
                                        final TextView content = (TextView) infoWindow.findViewById(R.id.content);
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
                                                                for (Restaurant restaurant : restaurants.getResults()) {
                                                                    if (restaurant.getName() != null) {
                                                                        Log.d("Restaurant", "name: "+restaurant.getName());
                                                                        if (restaurant.getLatitude() == mark.getPosition().latitude
                                                                                && restaurant.getLongitude() == mark.getPosition().longitude) {

                                                                            if(mListener != null)
                                                                                mListener.showRestaurantDetails(restaurant.getId());

                                                                            break;
                                                                        }
                                                                    }

                                                                }

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
                                    }
                                }
                                mUtils.dismissDialog(mProgressDialog);
                            } catch (Exception e) {
                                e.printStackTrace();
                                mUtils.dismissDialog(mProgressDialog);
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            mUtils.dismissDialog(mProgressDialog);
                            mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_cannot_process_request)).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    mUtils.dismissDialog(mProgressDialog);
                }

                LatLng userPosition = new LatLng(latitude, longitude);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(userPosition)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                mMap.addMarker(markerOptions);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 10));
            }
        });
    }

    protected LocationRequest createLocationRequest() {
        Log.d(LOGGER_TAG, "createLocationRequest");

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(30000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return mLocationRequest;
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

            if(mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if(mGoogleApiClient != null) {
            if(mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
                mGoogleApiClient.disconnect();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        VolleySingleton.getInstance(getContext()).getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
        if(mGoogleApiClient != null) {
            if(mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
                mGoogleApiClient.disconnect();
            }
        }
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
}
