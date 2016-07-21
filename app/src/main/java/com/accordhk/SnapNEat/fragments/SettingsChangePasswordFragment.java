package com.accordhk.SnapNEat.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.models.BaseResponse;
import com.accordhk.SnapNEat.models.User;
import com.accordhk.SnapNEat.services.ApiWebServices;
import com.accordhk.SnapNEat.utils.Constants;
import com.accordhk.SnapNEat.utils.CustomFontEditText;
import com.accordhk.SnapNEat.utils.CustomFontTextView;
import com.accordhk.SnapNEat.utils.VolleySingleton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsChangePasswordFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsChangePasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsChangePasswordFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SettingsChangePasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsChangePasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsChangePasswordFragment newInstance(String param1, String param2) {
        SettingsChangePasswordFragment fragment = new SettingsChangePasswordFragment();
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
        View view = inflater.inflate(R.layout.fragment_settings_change_password, container, false);

        CustomFontTextView action_bar_title = (CustomFontTextView) view.findViewById(R.id.action_bar_title);
        action_bar_title.setText(mUtils.getStringResource(R.string.a12_change_password));

        CustomFontTextView btn_submit = (CustomFontTextView) view.findViewById(R.id.btn_submit);
        btn_submit.setText(mUtils.getStringResource(R.string.a12_save));

        ImageButton btn_back = (ImageButton) view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.goBack();
                }
            }
        });

        final CustomFontEditText et_old_password = (CustomFontEditText) view.findViewById(R.id.et_old_password);
        final CustomFontEditText et_new_password = (CustomFontEditText) view.findViewById(R.id.et_new_password);
        final CustomFontEditText et_conf_new_password = (CustomFontEditText) view.findViewById(R.id.et_conf_new_password);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String oldPassword = et_old_password.getText().toString().trim();
                String newPassword = et_new_password.getText().toString().trim();
                String confNewPassword = et_conf_new_password.getText().toString().trim();

                //String errorMessage = mUtils.validatePassword(oldPassword, true);

//                if(errorMessage.isEmpty()) {
//                    errorMessage = mUtils.validatePasswordAndConfPassword(newPassword, confNewPassword);
//                }

                String errorMessage = mUtils.validatePasswordAndConfPassword(newPassword, confNewPassword);
                if(!errorMessage.isEmpty()) {
                    mUtils.getErrorDialog(errorMessage).show();
                } else {

                    try {
                        //String encPass = AESHelper.encrypt(Constants.SEED_VALUE, oldPassword);
                        //String encNewPass = AESHelper.encrypt(Constants.SEED_VALUE, newPassword);

                        Map<String, String> params = mUtils.getBaseRequestMap();
                        params.put(User.PASSWORD, oldPassword);
                        params.put(User.NEW_PASSWORD, newPassword);

                        try {
                            mProgressDialog.show();
                            mApi.changePassword(params, mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
                                @Override
                                public void onResponse(Object object) {
                                    try {
                                        BaseResponse res = (BaseResponse) object;
                                        mUtils.dismissDialog(mProgressDialog);

                                        if(res != null) {

                                            if(res.getStatus() == Constants.RES_UNAUTHORIZED) {
                                                if (mListener != null) {
                                                    mListener.showStartingFragmentFromLogout();
                                                }
                                            } else if(res.getStatus() == Constants.RES_SUCCESS) {
                                                mUtils.getDialog(res.getMessage()).show();
                                                if(mListener != null)
                                                    mListener.showSettingsFragment();
                                            } else {
                                                mUtils.getErrorDialog(res.getMessage()).show();
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
                                    error.printStackTrace();
                                    mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_cannot_process_request)).show();
                                }
                            });
                        } catch (Exception e){
                            mUtils.dismissDialog(mProgressDialog);
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

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
        void showSettingsFragment();
        void goBack();
        void showStartingFragmentFromLogout();
    }
}
