package com.accordhk.SnapNEat.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.adapters.RestaurantsMoreRowAdapter;
import com.accordhk.SnapNEat.models.ResponseBaseWithId;
import com.accordhk.SnapNEat.models.ResponseRestaurants;
import com.accordhk.SnapNEat.models.Restaurant;
import com.accordhk.SnapNEat.services.ApiWebServices;
import com.accordhk.SnapNEat.utils.Constants;
import com.accordhk.SnapNEat.utils.CustomFontEditText;
import com.accordhk.SnapNEat.utils.VolleySingleton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RestaurantListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RestaurantListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RestaurantListFragment extends BaseFragment {

    public static final String RESTAURANT = "restaurant";
    private String LOGGER_TAG = "RestaurantListFragment";
    private OnFragmentInteractionListener mListener;

    public static final int REQUEST_CHECK_SETTINGS = 4888;
    public static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 411;
    public static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 412;

    private float mLatitude;
    private float mLongitude;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private LocationListener mLocationListener;

    public Restaurant restaurantFilters;

    Restaurant mapSelectedRestaurant;

    private String mCurrentAddress;

    private View view;

    public RestaurantListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RestaurantListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RestaurantListFragment newInstance() {
        RestaurantListFragment fragment = new RestaurantListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            restaurantFilters = new Gson().fromJson(getArguments().getString(RESTAURANT), Restaurant.class);
        }

        mCurrentAddress = "";

        try {
            Log.d(LOGGER_TAG, String.valueOf(restaurantFilters));
        } catch (Exception e) {}

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(LOGGER_TAG, "Location changed: "+String.valueOf(location.getLatitude())+"--"+String.valueOf(location.getLongitude()));
                mLatitude = (float) location.getLatitude();
                mLongitude = (float) location.getLongitude();

                getGeoCode();
            }
        };

        // START: GET CURRENT LOCATION
        try {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext(), new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(@Nullable Bundle bundle) {
                    Log.d(LOGGER_TAG, "mGoogleApiClient connected!!!");

                    String provider = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                    if(provider.isEmpty()) {
                        getActivity().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }

                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient.isConnected()) {
                            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                            if (mLastLocation != null) {
                                Log.d(LOGGER_TAG, "Last known location");
                                mLatitude = (float) mLastLocation.getLatitude();
                                mLongitude = (float) mLastLocation.getLongitude();

                                getGeoCode();

                            } else {
                                Log.d(LOGGER_TAG, "request Location updates");

                                mLocationRequest = createLocationRequest();
                                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationListener);
                            }
                        }
                    } else {

                        Log.d(LOGGER_TAG, "Asking for permission");
                        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Log.d(LOGGER_TAG, "Check permission for ACCESS_COARSE_LOCATION");
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_ACCESS_COARSE_LOCATION);
                        }
                        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Log.d(LOGGER_TAG, "Check permission for ACCESS_FINE_LOCATION");
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
                        }

                    }
                }

                @Override
                public void onConnectionSuspended(int i) {

                }
            }, new GoogleApiClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                }
            }).addApi(LocationServices.API).build();

            mGoogleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // END: GET CURRENT LOCATION

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_restaurant_list, container, false);
        generateView();

        return view;
    }

    public void generateView() {
        final ListView lv_resto = (ListView) view.findViewById(R.id.lv_resto);

        lv_resto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mapSelectedRestaurant = (Restaurant) parent.getAdapter().getItem(position);

                Log.d(LOGGER_TAG, "selectedID: " + String.valueOf(mapSelectedRestaurant.getId()));

                if (mapSelectedRestaurant.getId() == -1) { // add new restaurant
                    Log.d("Add restaurant:", "YOSH");
                    try {
                        Map<String, String> params = mUtils.getBaseRequestMap();

                        // the orig name is in description
                        params.put(Restaurant.NAME, mapSelectedRestaurant.getDescription());
                        params.put(Restaurant.LATITUDE, String.valueOf(mLatitude));
                        params.put(Restaurant.LONGITUDE, String.valueOf(mLongitude));

                        mProgressDialog.show();
                        mApi.postNewRestaurant(params, mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
                            @Override
                            public void onResponse(Object object) {
                                try {
                                    ResponseBaseWithId response = (ResponseBaseWithId) object;
                                    mUtils.dismissDialog(mProgressDialog);
                                    if (response != null) {
                                        if(response.getStatus() == Constants.RES_UNAUTHORIZED) {
                                            if (mListener != null) {
                                                mListener.showStartingFragmentFromLogout();
                                            }
                                        } else if (response.getStatus() != Constants.RES_SUCCESS) {
                                            mUtils.getErrorDialog(response.getMessage()).show();
                                        } else {
                                            restaurantFilters.setId(response.getId());
                                            restaurantFilters.setName(mapSelectedRestaurant.getDescription());
                                            restaurantFilters.setLatitude(mLatitude);
                                            restaurantFilters.setLongitude(mLongitude);
                                            restaurantFilters.setLocation(mCurrentAddress);

                                            if (mListener != null)
                                                mListener.goBackResto(restaurantFilters);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
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
                } else { // restaurants displayed are from CMS

//                    new SharedPref(getContext()).setSelectedRestaurant(mapSelectedRestaurant);
                    restaurantFilters = mapSelectedRestaurant;

                    for (int x = 0; x < lv_resto.getAdapter().getCount(); x++) {
                        View v = lv_resto.getChildAt(x);
                        try {
                            (v.findViewById(R.id.iv_check)).setVisibility(View.INVISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    (view.findViewById(R.id.iv_check)).setVisibility(View.VISIBLE);
                }

            }
        });

        ImageButton btn_back = (ImageButton) view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.goBackResto(restaurantFilters);
            }
        });

        ImageButton btn_location = (ImageButton) view.findViewById(R.id.btn_location);
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) {
                    mListener.showRestaurantsMapFragment();
                }
            }
        });

        final CustomFontEditText et_resto_search = (CustomFontEditText) view.findViewById(R.id.et_resto_search);

        try {
            Map<String, String> params = mUtils.getBaseRequestMap();
            params.put(Constants.STR_PAGE, String.valueOf(-1));

            mProgressDialog.show();
            mApi.getRestaurants(params, mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
                @Override
                public void onResponse(Object object) {
                    try {
                        final ResponseRestaurants restaurants = (ResponseRestaurants) object;

                        if (restaurants != null) {
                            if(restaurants.getStatus() == Constants.RES_UNAUTHORIZED) {
                                if (mListener != null) {
                                    mUtils.dismissDialog(mProgressDialog);
                                    mListener.showStartingFragmentFromLogout();
                                }
                            } else if (restaurants.getStatus() != Constants.RES_SUCCESS) {
                                mUtils.dismissDialog(mProgressDialog);
                                mUtils.getErrorDialog(restaurants.getMessage()).show();
                            } else {
                                final RestaurantsMoreRowAdapter adapter = new RestaurantsMoreRowAdapter(getContext(), R.layout.list_new_resto_check_image_row
                                        , restaurants.getResults(), restaurantFilters, mCurrentAddress);

                                lv_resto.setAdapter(adapter);
                                lv_resto.setTextFilterEnabled(true);

                                et_resto_search.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        adapter.getFilter().filter(s.toString());
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });
                            }
                        }
                        mUtils.dismissDialog(mProgressDialog);
                    } catch (Exception e) {
                        e.printStackTrace();

                        try {
                            ResponseBaseWithId response = (ResponseBaseWithId) object;
                            if (response != null) {
                                if (response.getStatus() != Constants.RES_SUCCESS) {
                                    mUtils.dismissDialog(mProgressDialog);
                                    mUtils.getErrorDialog(response.getMessage()).show();
                                } else {
                                    restaurantFilters.setId(response.getId());
                                    restaurantFilters.setName(mapSelectedRestaurant.getDescription());
                                    restaurantFilters.setLatitude(mLatitude);
                                    restaurantFilters.setLongitude(mLongitude);
                                    restaurantFilters.setLocation(mCurrentAddress);

                                    mUtils.dismissDialog(mProgressDialog);

                                    if (mListener != null)
                                        mListener.goBackResto(restaurantFilters);
                                }
                            }
                        } catch (Exception e1) {
                            mUtils.dismissDialog(mProgressDialog);
                            e1.printStackTrace();
                        }
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
            mUtils.dismissDialog(mProgressDialog);
        }
    }

    protected LocationRequest createLocationRequest() {
        Log.d(LOGGER_TAG, "createLocationRequest");

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return mLocationRequest;
    }

    private void getGeoCode() {
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(getContext(), Locale.getDefault());

            Log.d("LatLong", mLatitude+"-"+mLongitude);

            addresses = geocoder.getFromLocation(mLatitude, mLongitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            //String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

            if(address != null)
                mCurrentAddress = address.trim();
            if(city != null) {
                if(mCurrentAddress.trim().isEmpty() == false)
                    mCurrentAddress += ", " + city.trim();
                else
                    mCurrentAddress = city.trim();
            }
            if(state != null) {
                if(mCurrentAddress.trim().isEmpty() == false)
                    mCurrentAddress += ", " + state.trim();
                else
                    mCurrentAddress = state.trim();
            }
            if(country != null) {
                if(mCurrentAddress.trim().isEmpty() == false)
                    mCurrentAddress += ", " + country.trim();
                else
                    mCurrentAddress = country.trim();
            }
            if(postalCode != null) {
                if(mCurrentAddress.trim().isEmpty() == false)
                    mCurrentAddress += " " + postalCode.trim();
                else
                    mCurrentAddress = postalCode.trim();
            }

            Log.d("CurrentAddress", mCurrentAddress);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == PackageManager.PERMISSION_GRANTED) {
            Log.d(LOGGER_TAG, "mGoogleApiClient null? "+(mGoogleApiClient==null));
            if(mGoogleApiClient != null)
                mGoogleApiClient.connect();
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
        void showRestaurantsMapFragment();
        void goBackResto(Restaurant resto);
        void showStartingFragmentFromLogout();
    }
}
