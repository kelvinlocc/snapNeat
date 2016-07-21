package com.accordhk.SnapNEat.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.adapters.ProfileFootprintsRowAdapter;
import com.accordhk.SnapNEat.models.District;
import com.accordhk.SnapNEat.models.ResponseFootprints;
import com.accordhk.SnapNEat.models.User;
import com.accordhk.SnapNEat.services.ApiWebServices;
import com.accordhk.SnapNEat.utils.Constants;
import com.accordhk.SnapNEat.utils.CustomFontTextView;
import com.accordhk.SnapNEat.utils.Utils;
import com.accordhk.SnapNEat.utils.VolleySingleton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFootprintsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFootprintsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFootprintsFragment extends BaseSearchFragment {

    private static String LOGGER_TAG = "ProfileFootprintsFragment";
    public static final String USER_ID = "userId";

    private int userId;

    private OnFragmentInteractionListener mListener;

    public ProfileFootprintsFragment() {
        // Required empty public constructor
    }

    /* GPS Constant Permission */
    public static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    public static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;

    public static final int REQUEST_CHECK_SETTINGS = 3888;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private LocationListener mLocationListener;

    private View view;
    private ListView ll_footprints;
    private CustomFontTextView message;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 User ID.
     * @return A new instance of fragment ProfileFootprintsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFootprintsFragment newInstance(int param1) {
        ProfileFootprintsFragment fragment = new ProfileFootprintsFragment();
        Bundle args = new Bundle();
        args.putInt(USER_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt(USER_ID);
        }

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(LOGGER_TAG, "Location changed: "+String.valueOf(location.getLatitude())+"--"+String.valueOf(location.getLongitude()));
                getFootprints(location.getLatitude(), location.getLongitude());
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile_footprints, container, false);
        ll_footprints = (ListView) view.findViewById(R.id.ll_footprints);
        message = (CustomFontTextView) view.findViewById(R.id.message);

        page = 1;

        try {

            doGoogleConnect();

        } catch (Exception e) {
            e.printStackTrace();
            //mUtils.dismissDialog(mProgressDialog);
            ll_footprints.setVisibility(View.INVISIBLE);
            message.setVisibility(View.VISIBLE);
            message.setText(mUtils.getStringResource(R.string.error_cannot_process_request));
        }

        return view;
    }

    private void doGoogleConnect() {
        if (mGoogleApiClient == null) {

            mGoogleApiClient = new GoogleApiClient.Builder(getContext(), new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle bundle) {
                    Log.d(LOGGER_TAG, "mGoogleApiClient connected!!!");

                    String provider = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                    if(provider.isEmpty()) {
                        getActivity().startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_CHECK_SETTINGS);
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
                                    getFootprints(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                                } else {
                                    Log.d(LOGGER_TAG, "request Location updates");

                                    mLocationRequest = createLocationRequest();
                                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationListener);
                                }
                            } else
                                mGoogleApiClient.connect();
                        }

                    }
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

        }
    }

    private void getFootprints(double lat, double lng) {
        try {
            Map<String, String> params = mUtils.getBaseRequestMap();
            params.put(Constants.STR_PAGE, String.valueOf(page));
            params.put(User.USER_ID, String.valueOf(userId));
            params.put(District.LATITUDE, String.valueOf(lat));
            params.put(District.LONGITUDE, String.valueOf(lng));
//            params.put(District.LATITUDE, String.valueOf(1.350772));
//            params.put(District.LONGITUDE, String.valueOf(103.848183));

            //mProgressDialog.show();
            mApi.getFootprints(params, mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
                @Override
                public void onResponse(Object object) {
                    try {
                        ResponseFootprints response = (ResponseFootprints) object;
                        page = response.getCurrentPageNo();

                        if(response.getStatus() == Constants.RES_UNAUTHORIZED) {
                            if(mListener != null) {
                                mListener.showStartingFragmentFromLogout();
                            }
                        } else if (response.getStatus() != Constants.RES_SUCCESS) {
                            ll_footprints.setVisibility(View.INVISIBLE);
                            message.setVisibility(View.VISIBLE);
                            message.setText(response.getMessage());
                        } else {
                            Log.d("result size: ", response.getResults().size() + "");

                            if (response.getResults().size() > 0) {
                                ll_footprints.setVisibility(View.VISIBLE);
                                message.setVisibility(View.INVISIBLE);

                                final ProfileFootprintsRowAdapter adapter = new ProfileFootprintsRowAdapter(getContext(), R.layout.list_footprints_row, response.getResults(), new ProfileFootprintsRowAdapter.ProfileFootprintsRowAdapterListener() {
                                    @Override
                                    public void showSnapDetails(int snapId) {
                                        if (mListener != null) {
                                            mListener.showSnapDetails(snapId);
                                        }
                                    }
                                });

                                ll_footprints.setAdapter(adapter);
                                ll_footprints.setOnScrollListener(new AbsListView.OnScrollListener() {

                                    private int currentVisibleItemCount;
                                    private int currentScrollState;
                                    private int currentFirstVisibleItem;
                                    private int totalItem;

                                    @Override
                                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                                        this.currentScrollState = scrollState;
                                        if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                                                && this.currentScrollState == SCROLL_STATE_IDLE) {

                                            page++;

                                            try {

                                                Map<String, String> params = mUtils.getBaseRequestMap();
                                                params.put(Constants.STR_PAGE, String.valueOf(page));
                                                params.put(User.USER_ID, String.valueOf(userId));

                                                //mProgressDialog.show();
                                                mApi.getFootprints(params, mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
                                                    @Override
                                                    public void onResponse(Object object) {
                                                        try {
                                                            ResponseFootprints response = (ResponseFootprints) object;
                                                            page = response.getCurrentPageNo();

                                                            if(response.getStatus() == Constants.RES_UNAUTHORIZED) {
                                                                if(mListener != null) {
                                                                    mListener.showStartingFragmentFromLogout();
                                                                }
                                                            } else if (response.getResults().size() > 0) {
                                                                adapter.addAll(response.getResults());
                                                                adapter.notifyDataSetChanged();
                                                            }
                                                            //mUtils.dismissDialog(mProgressDialog);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        error.printStackTrace();
                                                        //mUtils.dismissDialog(mProgressDialog);
                                                        mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_cannot_process_request)).show();
                                                    }
                                                });
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                //mUtils.dismissDialog(mProgressDialog);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                                        this.currentFirstVisibleItem = firstVisibleItem;
                                        this.currentVisibleItemCount = visibleItemCount;
                                        this.totalItem = totalItemCount;
                                    }
                                });
                            } else {
    //                                mUtils.getDialog(mUtils.getStringResource(R.string.error_no_data)).show();
                                ll_footprints.setVisibility(View.INVISIBLE);
                                message.setVisibility(View.VISIBLE);
                                message.setText(response.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //mUtils.dismissDialog(mProgressDialog);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    //mUtils.dismissDialog(mProgressDialog);
                    ll_footprints.setVisibility(View.INVISIBLE);
                    message.setVisibility(View.VISIBLE);
                    message.setText(mUtils.getStringResource(R.string.error_cannot_process_request));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            //mUtils.dismissDialog(mProgressDialog);
            ll_footprints.setVisibility(View.INVISIBLE);
            message.setVisibility(View.VISIBLE);
            message.setText(mUtils.getStringResource(R.string.error_cannot_process_request));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == PackageManager.PERMISSION_GRANTED) {
            Log.d(LOGGER_TAG, "mGoogleApiClient null? "+(mGoogleApiClient==null));
            if(mGoogleApiClient != null)
                mGoogleApiClient.connect();
            doGoogleConnect();
        }

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

        if(mGoogleApiClient != null) {
            if(mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
                mGoogleApiClient.disconnect();
            }
        }

        VolleySingleton.getInstance(getContext()).getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }

    protected LocationRequest createLocationRequest() {
        Log.d(LOGGER_TAG, "createLocationRequest");

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000);
        mLocationRequest.setFastestInterval(60000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return mLocationRequest;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_COARSE_LOCATION:
            case MY_PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mGoogleApiClient.connect();
                } else {
                    Toast.makeText(getContext(), (new Utils(getActivity())).getStringResource(R.string.common_permission_denied), Toast.LENGTH_SHORT).show();
                }
                break;
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
        void showSnapDetails(int snapId);
        void showStartingFragmentFromLogout();
    }
}
