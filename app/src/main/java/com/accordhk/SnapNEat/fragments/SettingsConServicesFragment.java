package com.accordhk.SnapNEat.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.models.ResponseLogin;
import com.accordhk.SnapNEat.models.ResponseUserProfile;
import com.accordhk.SnapNEat.models.User;
import com.accordhk.SnapNEat.services.ApiWebServices;
import com.accordhk.SnapNEat.utils.Constants;
import com.accordhk.SnapNEat.utils.CustomFontTextView;
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
 * {@link SettingsConServicesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsConServicesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsConServicesFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final String LOGGER_TAG = "StartingFragment";

    public static final int FACEBOOK_REQUEST_CODE = 129742;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private CallbackManager mCallbackManager;

    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;

    private SharedPref sharedPref;

    public SettingsConServicesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsConServicesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsConServicesFragment newInstance(String param1, String param2) {
        SettingsConServicesFragment fragment = new SettingsConServicesFragment();
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

        sharedPref = new SharedPref(getContext());
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
        View view = inflater.inflate(R.layout.fragment_settings_con_services, container, false);

        CustomFontTextView action_bar_title = (CustomFontTextView) view.findViewById(R.id.action_bar_title);
        action_bar_title.setText(mUtils.getStringResource(R.string.a6_connected_services));

        ImageButton btn_back = (ImageButton) view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.goBack();
                }
            }
        });

        CustomFontTextView tv_username = (CustomFontTextView) view.findViewById(R.id.tv_username);
        sharedPref = new SharedPref(getContext());
        if(sharedPref.getIsFacebookUser() == true) {
            User user = sharedPref.getLoggedInUser();
            if(user != null) {
                tv_username.setText(user.getUsername());
            }

        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                if(sharedPref.getIsFacebookUser()) // current fb login
                    builder.setMessage(mUtils.getStringResource(R.string.disconnect_fb));
                else // normal login user
                    builder.setMessage(mUtils.getStringResource(R.string.disconnect_login_normal));

                builder.setPositiveButton(mContext.getResources().getString(R.string.common_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                        if(sharedPref.getIsFacebookUser()) { // current fb login
                            mUtils.resetPreference();

                            if(mListener != null) {
                                mListener.showStartingFragmentFromLogout();
                            }

                        } else { // normal login user
                            mUtils.resetPreference();
                            LoginManager.getInstance().logInWithReadPermissions(SettingsConServicesFragment.this, Arrays.asList("public_profile", "email"));
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
                                                    String udid = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

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
//                                                                        mUtils.getErrorDialog(mUtils.getErrorMessage(responseLogin.getStatus(), mUtils.getMessageValue(responseLogin.getMessage()))).show();
                                                                            mUtils.dismissDialog(mProgressDialog);
                                                                            mUtils.getErrorDialog(responseLogin.getMessage()).show();
                                                                        } else {
                                                                            SharedPref sharedPref = new SharedPref(getContext());
                                                                            sharedPref.setSessionString(responseLogin.getSessionString());
                                                                            sharedPref.setIsFacebookUser(true);

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
                    }
                });

                builder.setNegativeButton(mContext.getResources().getString(R.string.common_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();

                dialog.show();
            }
        };

        LinearLayout ll_connected_services = (LinearLayout) view.findViewById(R.id.ll_connected_services);
        ll_connected_services.setOnClickListener(onClickListener);

        ImageButton btn_con_fb = (ImageButton) view.findViewById(R.id.btn_con_fb);
        btn_con_fb.setOnClickListener(onClickListener);

        return view;
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
                                    mListener.showHomeFragment();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
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
        void showHomeFragment();
        void updateNavigationDrawerItems();
        void showStartingFragmentFromLogout();
    }
}
