package com.accordhk.SnapNEat.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.adapters.SnapHomepageAdapter;
import com.accordhk.SnapNEat.models.ResponsePostLike;
import com.accordhk.SnapNEat.models.ResponseSnapsHomepage;
import com.accordhk.SnapNEat.models.Snap;
import com.accordhk.SnapNEat.models.User;
import com.accordhk.SnapNEat.services.ApiWebServices;
import com.accordhk.SnapNEat.utils.Constants;
import com.accordhk.SnapNEat.utils.CustomFontButton;
import com.accordhk.SnapNEat.utils.CustomFontTextView;
import com.accordhk.SnapNEat.utils.SharedPref;
import com.accordhk.SnapNEat.utils.Utils;
import com.accordhk.SnapNEat.utils.VolleySingleton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wenchao.cardstack.CardStack;

import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends BaseFragment {
    private static String LOGGER_TAG = "MainFragment";
    private int MODE_RANDOM = Constants.HOMEPAGE_SNAP_MODE.RANDOM.getKey();
    private int MODE_LOCATION = Constants.HOMEPAGE_SNAP_MODE.LOCATION.getKey();

    /* GPS Constant Permission */
    public static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    public static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;

    public static final int REQUEST_CHECK_SETTINGS = 2888;

    /* Position */
    private static final int MINIMUM_TIME = 10000;  // 10s
    private static final int MINIMUM_DISTANCE = 50; // 50m

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    private CardStack mCardStack;

    /* GPS */
    private String mProviderName;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    private RelativeLayout rl_share;
    private RelativeLayout rl_choose_distance;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    // wechat
    private IWXAPI iwapi;

    private View view;

    private int distance;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        distance = 1;

        FacebookSdk.sdkInitialize(getContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {

            @Override
            public void onSuccess(Sharer.Result result) {
                Log.d(LOGGER_TAG, "success");
            }

            @Override
            public void onCancel() {
                Log.e(LOGGER_TAG, "cancel");
            }

            @Override
            public void onError(FacebookException e) {
                e.printStackTrace();
            }

        });

        // start: wechat
        iwapi = WXAPIFactory.createWXAPI(getContext(), Constants.WECHAT_APPID, false);
        // end: wechat

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(LOGGER_TAG, "Location changed: "+String.valueOf(location.getLatitude())+"--"+String.valueOf(location.getLongitude()));
                getSnaps(MODE_LOCATION, location.getLatitude(), location.getLongitude());
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);
        mCardStack = (CardStack) view.findViewById(R.id.card_stack);
        rl_share = (RelativeLayout) view.findViewById(R.id.rl_share);

        rl_choose_distance = (RelativeLayout) view.findViewById(R.id.rl_choose_distance);

        RelativeLayout rl_share_fb = (RelativeLayout) view.findViewById(R.id.rl_share_fb);
        RelativeLayout rl_share_instagram = (RelativeLayout) view.findViewById(R.id.rl_share_instagram);
        RelativeLayout rl_share_wechat = (RelativeLayout) view.findViewById(R.id.rl_share_wechat);

        // custom action bar
        ImageButton btn_menu = (ImageButton) view.findViewById(R.id.btn_menu);
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.toggleSideBarNav();
                }
            }
        });

        ImageButton btn_by_distance = (ImageButton) view.findViewById(R.id.btn_by_distance);
        btn_by_distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideUp(rl_choose_distance);
            }
        });

        CustomFontTextView tv_distance_1km = (CustomFontTextView) rl_choose_distance.findViewById(R.id.tv_distance_1km);
        tv_distance_1km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSnapsByDistance(1);
            }
        });

        CustomFontTextView tv_distance_2km = (CustomFontTextView) rl_choose_distance.findViewById(R.id.tv_distance_2km);
        tv_distance_2km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSnapsByDistance(2);
            }
        });

        CustomFontTextView tv_distance_3km = (CustomFontTextView) rl_choose_distance.findViewById(R.id.tv_distance_3km);
        tv_distance_3km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSnapsByDistance(3);
            }
        });

        CustomFontTextView tv_distance_4km = (CustomFontTextView) rl_choose_distance.findViewById(R.id.tv_distance_4km);
        tv_distance_4km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSnapsByDistance(4);
            }
        });

        CustomFontTextView tv_distance_5km = (CustomFontTextView) rl_choose_distance.findViewById(R.id.tv_distance_5km);
        tv_distance_5km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSnapsByDistance(5);
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

        ImageButton btn_search = (ImageButton) view.findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    Map<String, String> params = mUtils.getBaseRequestMap();
//                    SharedPref sharedPref = new SharedPref(getContext());
//                    sharedPref.setSelectedMoreHotSearchFilters(HotSearch.Category.DISH.getKey(), new ArrayList<String>());
//                    sharedPref.setSelectedMoreHotSearchFilters(HotSearch.Category.SPENDINGS.getKey(), new ArrayList<String>());
//                    sharedPref.setSelectedMoreHotSearchFilters(HotSearch.Category.DISTRICT.getKey(), new ArrayList<String>());
//                    sharedPref.setSelectedMoreHotSearchFilters(HotSearch.Category.HASHTAGS.getKey(), new ArrayList<String>());

                    mProgressDialog.show();

                    mApi.getHotSearchFilters(params, new ApiWebServices.ApiListener() {
                        @Override
                        public void onResponse(Object object) {
                            mUtils.dismissDialog(mProgressDialog);
                            if (mListener != null) {
                                mListener.showHotSearchListFragment();
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mUtils.dismissDialog(mProgressDialog);
                            error.printStackTrace();
                        }
                    }, true);

                } catch (Exception e) {
                    e.printStackTrace();
                    mUtils.dismissDialog(mProgressDialog);
                }
            }
        });

        ImageButton btn_by_location = (ImageButton) view.findViewById(R.id.btn_by_location);
        btn_by_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.showNearbyRestaurantsMapFragment();
            }
        });

//        final ToggleButton btn_by_location = (ToggleButton) view.findViewById(R.id.btn_by_location);
//        btn_by_location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    Log.d(LOGGER_TAG, "by Location");
//
//                    Log.d(LOGGER_TAG, "mGoogleApiClient is null? " + (mGoogleApiClient == null));
//
//                    if (mGoogleApiClient == null) {
//
//                        mGoogleApiClient = new GoogleApiClient.Builder(getContext(), new ConnectionCallbacks() {
//                            @Override
//                            public void onConnected(Bundle bundle) {
//                                Log.d(LOGGER_TAG, "mGoogleApiClient connected!!!");
//                                btn_by_location.setButtonDrawable(R.drawable.btn_location_tint);
//                                getSnapsByLocation();
//                            }
//
//                            @Override
//                            public void onConnectionSuspended(int i) {
//
//                            }
//                        }, new OnConnectionFailedListener() {
//                            @Override
//                            public void onConnectionFailed(ConnectionResult connectionResult) {
//
//                            }
//                        }).addApi(LocationServices.API).build();
//                        mGoogleApiClient.connect();
//
//                    } else {
//                        btn_by_location.setButtonDrawable(R.drawable.btn_location_tint);
//                        getSnapsByLocation();
//                    }
//
//                } else {
//                    Log.d(LOGGER_TAG, "random");
//                    btn_by_location.setButtonDrawable(R.drawable.btn_location_white);
//                    getSnaps(MODE_RANDOM);
//                }
//            }
//        });

        ImageButton btn_facebook = (ImageButton) view.findViewById(R.id.btn_facebook);
        if(isPackageInstalled(Constants.FACEBOOK_PACKAGE))
            rl_share_fb.setVisibility(View.VISIBLE);
        else
            rl_share_fb.setVisibility(View.GONE);

        btn_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupBackStack.pop();
                slideDown(rl_share);
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    Snap s = (Snap) mCardStack.getAdapter().getItem(mCardStack.getCurrIndex());
                    try {
                        shareDialog.show(generateFBShareContent(s, true));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        ImageButton btn_wechat = (ImageButton) view.findViewById(R.id.btn_wechat);
        if (isPackageInstalled(Constants.WECHAT_PACKAGE))
            rl_share_wechat.setVisibility(View.VISIBLE);
        else
            rl_share_wechat.setVisibility(View.GONE);
        btn_wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupBackStack.pop();
                slideDown(rl_share);

                Snap s = (Snap) mCardStack.getAdapter().getItem(mCardStack.getCurrIndex());
                iwapi.sendReq(generateWeChatShareContent(s, true));
            }
        });

        ImageButton btn_instagram = (ImageButton) view.findViewById(R.id.btn_instagram);
        if(isPackageInstalled(Constants.INSTAGRAM_PACKAGE))
            rl_share_instagram.setVisibility(View.VISIBLE);
        else
            rl_share_instagram.setVisibility(View.GONE);

        btn_instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupBackStack.pop();
                slideDown(rl_share);

                Snap s = (Snap) mCardStack.getAdapter().getItem(mCardStack.getCurrIndex());
                // Create the new Intent using the 'Send' action.
                final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setPackage(Constants.INSTAGRAM_PACKAGE);

                // Set the MIME type
                shareIntent.setType("image/*");
                try {

                    if(s.getImage() != null && s.getImage().trim().isEmpty() == false) {
                        showPermitSaveExternal(shareIntent, s.getImage().trim());
                    } else {
                        mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_no_image_to_share)).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_no_image_to_share)).show();
                }

            }
        });

        ImageButton btn_others = (ImageButton) view.findViewById(R.id.btn_others);
        btn_others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupBackStack.pop();
                slideDown(rl_share);
                Snap s = (Snap) mCardStack.getAdapter().getItem(mCardStack.getCurrIndex());
                // Create the new Intent using the 'Send' action.
                final Intent shareIntent = new Intent(Intent.ACTION_SEND);

                // Set the MIME type
                shareIntent.setType("image/*");
                try {

                    if(s.getImage() != null && s.getImage().trim().isEmpty() == false) {
                        showPermitSaveExternal(shareIntent, s.getImage().trim());
                    } else {
                        mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_no_image_to_share)).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_no_image_to_share)).show();
                }
            }
        });
        CustomFontButton btn_cancel = (CustomFontButton) view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupBackStack.pop();
                slideDown(rl_share);
            }
        });

        // get snaps for homepage
        getSnaps(MODE_RANDOM);

        return view;
    }

    /**
     * Gets the snaps base of the mode
     * @param mode
     */
    private void getSnaps(int mode) {
        getSnaps(mode, 0.00, 0.00);
    }

    private void getSnaps(int mode, double lat, double lon) {
        Map<String, String> params = (new Utils(getActivity())).getBaseRequestMap();
        params.put(Snap.MODE, Integer.toString(mode));

        if (mode == MODE_LOCATION) {
            params.put(Snap.LATITUDE, String.valueOf(lat));
            params.put(Snap.LONGITUDE, String.valueOf(lon));
            params.put(Snap.DISTANCE, String.valueOf(distance));
        }

        User user = new SharedPref(mContext).getLoggedInUser();
        if(user != null) {
            params.put(Snap.USER_ID, String.valueOf(user.getId()));
        }

        try {

            mProgressDialog.show();

            mApi.getSnapsForHomepage(params, new ApiWebServices.ApiListener() {
                @Override
                public void onResponse(final Object obj) {
                    final ResponseSnapsHomepage res = (ResponseSnapsHomepage) obj;

                    final CustomFontTextView message = (CustomFontTextView) view.findViewById(R.id.message);

                    if (res.getStatus() == Constants.RES_SUCCESS) {

                        Log.d(LOGGER_TAG, "SIZE: " + String.valueOf(res.getResults().size()));

                        if(res.getResults().size() > 0) {
                            mCardStack.setVisibility(View.VISIBLE);
                            mCardStack.setContentResource(R.layout.card_stack_item);
                            //mCardStack.setStackMargin(60);

                            mCardStack.setAdapter(getCardAdapter(res.getResults()));
//                            mCardStack.setVisibleCardNum(res.getResults().size());
                            mCardStack.reset(true);
                            mCardStack.setListener(new CardStack.CardEventListener() {
                                @Override
                                public boolean swipeEnd(int section, float distance) {
                                    return (distance > 300) ? true : false;
                                }

                                @Override
                                public boolean swipeStart(int section, float distance) {
                                    return true;
                                }

                                @Override
                                public boolean swipeContinue(int section, float distanceX, float distanceY) {
                                    return true;
                                }

                                @Override
                                public void discarded(int mIndex, int direction) {
                                    // no more snaps
//                                    if (mIndex == mCardStack.getStackSize())
                                    if (mIndex == res.getResults().size())
                                        message.setText(mUtils.getStringResource(R.string.error_no_more_snap));
                                }

                                @Override
                                public void topCardTapped() {
                                    Snap snap = (Snap) mCardStack.getAdapter().getItem(mCardStack.getCurrIndex());
                                    if (mListener != null) {
                                        mListener.showSnapDetails(Integer.parseInt(snap.getnId()));
                                    }
                                }
                            });
                            mUtils.dismissDialog(mProgressDialog);
                        } else {
//                            mUtils.getDialog(mUtils.getStringResource(R.string.error_no_data)).show();
                            mCardStack.setVisibility(View.GONE);
                            mUtils.dismissDialog(mProgressDialog);
                            message.setText(res.getMessage());
//                            mUtils.getDialog(res.getMessage()).show();
                        }

                    } else {
                        // no results
//                        (new Utils(getActivity())).getErrorDialog(String.valueOf(res.getStatus()));
                        mCardStack.setVisibility(View.GONE);
                        mUtils.dismissDialog(mProgressDialog);
                        message.setText(res.getMessage());
                        mUtils.getErrorDialog(res.getMessage()).show();
                    }

                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    mProgressDialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            mProgressDialog.dismiss();
        }
    }

    protected LocationRequest createLocationRequest() {
        Log.d(LOGGER_TAG, "createLocationRequest");

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return mLocationRequest;
    }


//    private void showPermitSaveExternal(String strUrl){
//        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            ImageRequest ir = new ImageRequest(strUrl, new Response.Listener<Bitmap>() {
//
//                @Override
//                public void onResponse(Bitmap response) {
//                    try {
//                        Map<String, Object> bitmapResult = mUtils.processBitmap(getActivity(), response);
//
//                        if(bitmapResult.get(Constants.PHOTO_PATH).toString().trim().isEmpty() == false) {
//                            // Create the URI from the media
//                            File media = new File(bitmapResult.get(Constants.PHOTO_PATH).toString());
//                            Uri shareUri = Uri.fromFile(media);
//                            // Add the URI to the Intent.
//                            shareIntent.putExtra(Intent.EXTRA_STREAM, shareUri);
//
//                            // Broadcast the Intent.
//                            startActivity(Intent.createChooser(shareIntent, "Share to"));
//                        } else
//                            mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_no_image_to_share)).show();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_no_image_to_share)).show();
//                    }
//                }
//            }, 0, 0, null, null);
//
//            ir.setRetryPolicy(new DefaultRetryPolicy(
//                    30000,
//                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//            VolleySingleton.getInstance(getContext()).addToRequestQueue(ir);
//        } else {
//            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
//        }
//    }

    private void getSnapsByLocation() {

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
                        getSnaps(MODE_LOCATION, mLastLocation.getLatitude(), mLastLocation.getLongitude());

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
//                if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
//
//                } else
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_ACCESS_COARSE_LOCATION);
            }
            // The ACCESS_FINE_LOCATION is denied, then I request it and manage the result in
            // onRequestPermissionsResult() using the constant MY_PERMISSION_ACCESS_FINE_LOCATION
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(LOGGER_TAG, "Check permission for ACCESS_FINE_LOCATION");
//                if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
//
//                } else
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
            }

        }
    }


    /**
     * Generates new adapter
     * @param res
     * @return
     */
    private SnapHomepageAdapter getCardAdapter(List<Snap> res) {

        return new SnapHomepageAdapter(getContext(), R.layout.card_stack_item, res, new SnapHomepageAdapter.SnapCardListener() {
            @Override
            public void onProfilePicClick(View v, Snap s) {
                Log.d(LOGGER_TAG, "onProfilePicClick");
                if(mListener != null) {
                    mListener.showUserProfile(s.getUser().getId());
                }
            }

            @Override
            public void onShareClick(View v, Snap s) {
                Log.d(LOGGER_TAG, "onShareClick");
                slideUp(rl_share);
            }

            @Override
            public void onNavigateClick(View v, Snap s) {
                Log.d(LOGGER_TAG, "onNavigateClick");
                if (mListener != null) {
                    mListener.showGMapFragment(Integer.parseInt(s.getRestaurant().getnId()), s.getRestaurant().getLatitude(), s.getRestaurant().getLongitude(), s.getTitle(), s.getRestaurant().getName());
                }
            }

            @Override
            public void onLikeClick(View v, Snap s) {
                final RelativeLayout parentView = (RelativeLayout) v.getTag();

                User user = new SharedPref(getContext()).getLoggedInUser();
                if(user != null) { // user currently logged in
                    try {
                        final Map<String, String> params = mUtils.getBaseRequestMap();
                        params.put(Snap.SNAP_ID, String.valueOf(s.getnId()));

//                        mProgressDialog.show();
                        mApi.postSnapLike(params, mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
                            @Override
                            public void onResponse(Object obj) {
//                                mUtils.dismissDialog(mProgressDialog);
                                ResponsePostLike snapLikeRes = (ResponsePostLike) obj;

                                if(snapLikeRes.getStatus() == Constants.RES_UNAUTHORIZED) {
                                    if(mListener != null) {
                                        mListener.showStartingFragmentFromLogout();
                                    }
                                } else if(snapLikeRes.getStatus() != Constants.RES_SUCCESS) {
                                    mUtils.getErrorDialog(snapLikeRes.getMessage()).show();
                                } else {
                                    ImageButton btn_like = (ImageButton) parentView.findViewById(R.id.btn_like);
                                    CustomFontTextView like_count = (CustomFontTextView) parentView.findViewById(R.id.like_count);

                                    like_count.setText(String.valueOf(snapLikeRes.getTotalLikes()));

                                    if(snapLikeRes.getLikeStatus() == Constants.FLAG_TRUE)
                                        btn_like.setImageResource(R.drawable.s1_btn_like);
                                    else
                                        btn_like.setImageResource(R.drawable.s1_btn_like_default);
                                }

                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
//                                mUtils.dismissDialog(mProgressDialog);
                                Log.d(LOGGER_TAG, "Error in postSnapLike: " + error.getLocalizedMessage());
                            }
                        });
                    } catch (Exception e) {
//                        mUtils.dismissDialog(mProgressDialog);
                        Log.d(LOGGER_TAG, "Error in onLikeClick: " + e.getLocalizedMessage());
                    }
                } else {
//                    mUtils.dismissDialog(mProgressDialog);
                    if(mListener != null)
                        mListener.showLoginRegistrationFragment(true);
                }
            }
        });

    }

    private void getSnapsByDistance(final int dist) {
        Log.d(LOGGER_TAG, "mGoogleApiClient is null? " + (mGoogleApiClient == null));

        if (mGoogleApiClient == null) {

            mGoogleApiClient = new GoogleApiClient.Builder(getContext(), new ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle bundle) {
                    Log.d(LOGGER_TAG, "mGoogleApiClient connected!!!");
                    distance = dist;
                    mPopupBackStack.pop();
                    slideDown(rl_choose_distance);
                    getSnapsByLocation();
                }

                @Override
                public void onConnectionSuspended(int i) {

                }
            }, new OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(ConnectionResult connectionResult) {

                }
            }).addApi(LocationServices.API).build();
            mGoogleApiClient.connect();

        } else {
            distance = dist;
            mPopupBackStack.pop();
            slideDown(rl_choose_distance);
            getSnapsByLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_COARSE_LOCATION:
            case MY_PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mGoogleApiClient.connect();
//                    getSnapsByLocation();
                } else {
                    Toast.makeText(getContext(), (new Utils(getActivity())).getStringResource(R.string.common_permission_denied), Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case 0x101: {

            } default:
                callbackManager.onActivityResult(requestCode, resultCode, data);
                Log.d(LOGGER_TAG, "result " + ShareDialog.canShow(ShareLinkContent.class));
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

        iwapi.unregisterApp();

        VolleySingleton.getInstance(getContext()).getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
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
        void toggleSideBarNav();
        void showHotSearchListFragment();
        void showLoginRegistrationFragment(boolean showLoginFlag);
        void showUserProfile(int userId);
        void showSnapDetails(int id);
        void showGMapFragment(int id, float latitude, float longitude, String title, String name);
        void showStartingFragmentFromLogout();
        void showNearbyRestaurantsMapFragment();
    }
}
