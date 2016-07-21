package com.accordhk.SnapNEat.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.provider.Settings.Secure;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.models.ResponseLogin;
import com.accordhk.SnapNEat.models.ResponseUserProfile;
import com.accordhk.SnapNEat.models.User;
import com.accordhk.SnapNEat.services.ApiWebServices;
import com.accordhk.SnapNEat.utils.Constants;
import com.accordhk.SnapNEat.utils.SharedPref;
import com.accordhk.SnapNEat.utils.Utils;
import com.accordhk.SnapNEat.utils.VolleySingleton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StartingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StartingFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class StartingFragment extends BaseFragment {
    private final String LOGGER_TAG = "StartingFragment";

    public static final int FACEBOOK_REQUEST_CODE = 129742;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private CallbackManager mCallbackManager;

    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StartingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StartingFragment newInstance(String param1, String param2) {
        StartingFragment fragment = new StartingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public StartingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        FacebookSdk.sdkInitialize(getContext());
        mCallbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_starting, container, false);

        Button btn_guest_mode = (Button) view.findViewById(R.id.btn_guest_mode);
        btn_guest_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.showHomeFragmentNoBackStack();
                }
            }
        });

        Button btn_login = (Button) view.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.showLoginRegistrationFragment(true);
                }
            }
        });

        Button btn_register = (Button) view.findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.showLoginRegistrationFragment(false);
                }
            }
        });

        ImageButton btn_facebook = (ImageButton) view.findViewById(R.id.btn_facebook);
        btn_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOGGER_TAG, "facebook clicked!");
                LoginManager.getInstance().logInWithReadPermissions(StartingFragment.this, Arrays.asList("public_profile", "email"));
                LoginManager.getInstance().registerCallback(mCallbackManager,
                        new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                accessToken = loginResult.getAccessToken();

                                Log.d(LOGGER_TAG, "AccessToken: " + accessToken.toString());

                                GraphRequest request = GraphRequest.newMeRequest(accessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject user, final GraphResponse response) {

                                        String email = user.optString("email").trim();
                                        String fb_username = user.optString("name").trim().replace(" ", "")+mUtils.randomString(6, Utils.Mode.ALPHANUMERIC);

                                        String fb_uid = accessToken.getCurrentAccessToken().getUserId().trim();
                                        String fb_token = accessToken.getCurrentAccessToken().getToken().trim();
                                        int os_type = Constants.OS_TYPE;
                                        String udid = Secure.getString(getContext().getContentResolver(), Secure.ANDROID_ID);

                                        Log.v("fbemail", email);
                                        Log.v("fb_username", fb_username);
                                        Log.v("fb_uid", fb_uid);
                                        Log.v("fb_token", fb_token);

                                        try {
                                            Map<String, String> params = mUtils.getBaseRequestMap();
                                            params.put(User.EMAIL, email);
                                            params.put(User.FB_USERNAME, fb_username);
                                            params.put(User.FB_UID, fb_uid);
                                            params.put(User.FB_TOKEN, fb_token);
                                            if (Profile.getCurrentProfile()!=null) {
                                                params.put(User.FB_AVATAR_URL, Profile.getCurrentProfile().getProfilePictureUri(200, 200).toString());
                                            }
                                            params.put(User.OS_TYPE, String.valueOf(os_type));
                                            params.put(User.UDID, udid);

                                            mProgressDialog.show();
                                            mApi.fbloginUser(params, new ApiWebServices.ApiListener() {
                                                @Override
                                                public void onResponse(Object object) {
                                                    try {
                                                        ResponseLogin responseLogin = (ResponseLogin) object;

                                                        if (responseLogin != null) {
                                                            if (responseLogin.getStatus() != Constants.RES_SUCCESS) {
                                                                mUtils.getErrorDialog(responseLogin.getMessage()).show();
                                                            } else {
                                                                SharedPref sharedPref = new SharedPref(getContext());
                                                                sharedPref.setSessionString(responseLogin.getSessionString());
                                                                sharedPref.setIsFacebookUser(true);

                                                                // save profile pic
    //                                                            if (Profile.getCurrentProfile()!=null) {
    //                                                                String profilePic = Profile.getCurrentProfile().getProfilePictureUri(200, 200).toString();
    //                                                                Log.d(LOGGER_TAG, "PICTURE: " +profilePic);
    //                                                                sharedPref.setFBProfilePic(profilePic);
    //                                                            }

                                                                retrieveUserProfile(responseLogin.getUserId());
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
                                                    mUtils.dismissDialog(mProgressDialog);
                                                    mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_cannot_process_request)).show();
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            mUtils.dismissDialog(mProgressDialog);
                                        }

                                    }
                                });

                                Bundle parameters = new Bundle();
                                parameters.putString("fields", "name,email,picture");
                                request.setParameters(parameters);
                                request.executeAsync();
                            }

                            @Override
                            public void onCancel() {
                                // App code
                                Log.d(LOGGER_TAG, "FB onCancel");
                            }

                            @Override
                            public void onError(FacebookException exception) {
                                // App code
                                Log.d(LOGGER_TAG, "FB onError");
                                mUtils.getErrorDialog(exception.getMessage()).show();
                            }
                        });
            }
        });


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void retrieveUserProfile(int userId) {
        // get user profile
        try {
            Map<String, String> userProfileParam = mUtils.getBaseRequestMap();
            userProfileParam.put(User.USER_ID, String.valueOf(userId));

            mProgressDialog.show();
            mApi.getUserProfile(userProfileParam, mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
                @Override
                public void onResponse(Object object) {
                    try {
                        ResponseUserProfile responseUserProfile = (ResponseUserProfile) object;

                        mUtils.dismissDialog(mProgressDialog);
                        if (responseUserProfile != null) {
                            if(responseUserProfile.getStatus() == Constants.RES_UNAUTHORIZED) {
                                if (mListener != null) {
                                    mListener.showStartingFragmentFromLogout();
                                }
                            } else if (responseUserProfile.getStatus() != Constants.RES_SUCCESS) {
                                mUtils.getErrorDialog(responseUserProfile.getMessage()).show();
                            } else {
                                new SharedPref(getContext()).setLoggedInUser(responseUserProfile.getUserInfo());
                                if (mListener != null) {
                                    mListener.updateNavigationDrawerItems();
                                    mListener.showHomeFragmentNoBackStack();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUtils.dismissDialog(mProgressDialog);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    mUtils.dismissDialog(mProgressDialog);
                    mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_cannot_process_request)).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            mUtils.dismissDialog(mProgressDialog);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;

            AppEventsLogger.activateApp(getContext());

            // If the access token is available already assign it.
            accessToken = AccessToken.getCurrentAccessToken();

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

    @Override
    public void onPause() {
        super.onPause();
        VolleySingleton.getInstance(getContext()).getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(getContext());
        accessTokenTracker.stopTracking();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        /**
         * Show Login/Registration Page
         * @param showLoginFlag: true: Show Login tab; false: show Registration tab
         */
        void showLoginRegistrationFragment(boolean showLoginFlag);

        /**
         * Show Homepage
         */
        void showHomeFragment();
        void updateNavigationDrawerItems();
        void showStartingFragmentFromLogout();
        void showHomeFragmentNoBackStack();
    }
}
