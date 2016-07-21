package com.accordhk.SnapNEat.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.models.ResponseLogin;
import com.accordhk.SnapNEat.models.ResponseUserProfile;
import com.accordhk.SnapNEat.models.User;
import com.accordhk.SnapNEat.services.ApiWebServices;
import com.accordhk.SnapNEat.utils.Constants;
import com.accordhk.SnapNEat.utils.SharedPref;
import com.accordhk.SnapNEat.utils.VolleySingleton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends BaseFragment {

    private static String LOGGER_TAG = "LoginFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        final EditText et_email = (EditText) view.findViewById(R.id.et_email);
        final EditText et_password = (EditText) view.findViewById(R.id.et_password);

        Button btn_login = (Button) view.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = et_email.getText().toString().trim();
                String password = et_password.getText().toString().trim();

//                String errorMessage = mUtils.validateEmail(email, true);
//                if(errorMessage.isEmpty()) {
//                    errorMessage = mUtils.validatePassword(password, true);
//                }
//
//                if(!errorMessage.isEmpty()) {
//                    mUtils.getErrorDialog(errorMessage).show();
//                } else {

//                    try {
//                        String encPass = AESHelper.encrypt(Constants.SEED_VALUE, password);

//                        Log.d(LOGGER_TAG, "EncPass: " + encPass);

                        Map<String, String> params = mUtils.getBaseRequestMap();
                        params.put(User.EMAIL, email);
//                        params.put(User.PASSWORD, encPass);
                        params.put(User.PASSWORD, password);

                        Log.d(LOGGER_TAG, "UDID: "+ Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID));
                        params.put(User.UDID, Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID));
                        params.put(User.OS_TYPE, String.valueOf(Constants.OS_TYPE));

                        try {
                            mProgressDialog.show();
                            mApi.loginUser(params, new ApiWebServices.ApiListener() {
                                @Override
                                public void onResponse(Object object) {
                                    try {
                                        ResponseLogin response = (ResponseLogin) object;

                                        mUtils.dismissDialog(mProgressDialog);
                                        if (response != null) {
                                            if (response.getStatus() != Constants.RES_SUCCESS) {
                                                mUtils.getErrorDialog(response.getMessage()).show();
                                            } else {

                                                final SharedPref sharedPref = new SharedPref(getContext());
                                                sharedPref.setSessionString(response.getSessionString());

                                                // get user profile
                                                try {
                                                    Map<String, String> userProfileParam = mUtils.getBaseRequestMap();
    //                                                userProfileParam.put(User.USER_ID, String.valueOf(-1));
                                                    userProfileParam.put(User.USER_ID, String.valueOf(response.getUserId()));

                                                    mProgressDialog.show();
                                                    mApi.getUserProfile(userProfileParam, mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
                                                        @Override
                                                        public void onResponse(Object object) {
                                                            try {
                                                                ResponseUserProfile responseUserProfile = (ResponseUserProfile) object;

                                                                mUtils.dismissDialog(mProgressDialog);
                                                                if (responseUserProfile != null) {

                                                                    if(responseUserProfile.getStatus() == Constants.RES_UNAUTHORIZED) {
                                                                        if(mListener != null) {
                                                                            mListener.showStartingFragmentFromLogout();
                                                                        }
                                                                    } else if (responseUserProfile.getStatus() != Constants.RES_SUCCESS) {
                                                                        mUtils.getErrorDialog(responseUserProfile.getMessage()).show();
                                                                    } else {

                                                                        sharedPref.setIsFacebookUser(false);
                                                                        sharedPref.setLoggedInUser(responseUserProfile.getUserInfo());
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
                                                            error.printStackTrace();
                                                            mUtils.dismissDialog(mProgressDialog);
                                                            mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_cannot_process_request)).show();
                                                        }
                                                    });
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    mUtils.dismissDialog(mProgressDialog);
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
                                    error.printStackTrace();
                                    mUtils.dismissDialog(mProgressDialog);
                                    mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_cannot_process_request)).show();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            mUtils.dismissDialog(mProgressDialog);
                        }

//                    } catch (Exception e) {
//                        Log.e(LOGGER_TAG, "Error encountered during AESHelper.encrypt: " + e.getLocalizedMessage());
//                        mUtils.dismissDialog(mProgressDialog);
//                    }
//                }
            }
        });

        Button btn_forgot_password = (Button) view.findViewById(R.id.btn_forgot_password);
        btn_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.showResetPasswordFragment();
                }
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

    @Override
    public void onPause() {
        super.onPause();
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
        void showResetPasswordFragment();
        void showHomeFragment();
        boolean checkPlayServices();
        void updateNavigationDrawerItems();
        void showStartingFragmentFromLogout();
        void showHomeFragmentNoBackStack();
    }
}
