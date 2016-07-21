package com.accordhk.SnapNEat.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.models.BaseResponse;
import com.accordhk.SnapNEat.models.User;
import com.accordhk.SnapNEat.services.ApiWebServices;
import com.accordhk.SnapNEat.utils.Constants;
import com.accordhk.SnapNEat.utils.CustomFontEditText;
import com.accordhk.SnapNEat.utils.VolleySingleton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends BaseFragment {

    private static String LOGGER_TAG = "RegisterFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_register, container, false);
//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Button btn_register = (Button) view.findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomFontEditText et_username = (CustomFontEditText) view.findViewById(R.id.et_username);
                CustomFontEditText et_email = (CustomFontEditText) view.findViewById(R.id.et_email);
                CustomFontEditText et_password = (CustomFontEditText) view.findViewById(R.id.et_password);
                CustomFontEditText et_confirm_password = (CustomFontEditText) view.findViewById(R.id.et_confirm_password);

                String username = et_username.getText().toString().trim();
                String email = et_email.getText().toString().trim();
                String password = et_password.getText().toString().trim();
                String confPassword = et_confirm_password.getText().toString().trim();

                //check username
//                String errorMessage = mUtils.validateUsername(username, true);
//                if (errorMessage.isEmpty()) {
//                    // check email
//                    errorMessage = mUtils.validateEmail(email, true);
//                    if (errorMessage.isEmpty()) {
//                        errorMessage = mUtils.validatePasswordAndConfPassword(password, confPassword);
//                    }
//                }

                String errorMessage = mUtils.validatePasswordAndConfPassword(password, confPassword);

                if (!errorMessage.isEmpty()) {
                    mUtils.getErrorDialog(errorMessage).show();
                } else {

//                    try {
                        //String encPass = AESHelper.encrypt(Constants.SEED_VALUE, password);

                    Map<String, String> params = mUtils.getBaseRequestMap();
                    params.put(User.EMAIL, email);
                    params.put(User.USERNAME, username);
                    //params.put(User.PASSWORD, encPass);
                    params.put(User.PASSWORD, password);

                    try {
                        mProgressDialog.show();
                        mApi.registerUser(params, new ApiWebServices.ApiListener() {
                            @Override
                            public void onResponse(Object obj) {
                                BaseResponse res = (BaseResponse) obj;
                                mUtils.dismissDialog(mProgressDialog);
                                if (res != null) {
                                    if(res.getStatus() != Constants.RES_SUCCESS)
                                        mUtils.getErrorDialog(res.getMessage()).show();
                                    else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setMessage(res.getMessage());

                                        builder.setPositiveButton(getResources().getString(R.string.common_ok), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                                if(mListener != null)
                                                    mListener.selectLoginRegisterFragment(true);
                                            }
                                        });

                                        AlertDialog dialog = builder.create();
                                        dialog.show();

                                    }
                                }
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                mUtils.dismissDialog(mProgressDialog);
                                error.printStackTrace();
//                                    mUtils.getErrorDialog(error.getLocalizedMessage()).show();
                            }
                        });

                    } catch (Exception e) {
                        mUtils.dismissDialog(mProgressDialog);
                        e.printStackTrace();
                    }

//                    } catch (Exception e) {
//                        mUtils.dismissDialog(mProgressDialog);
//                        Log.e(LOGGER_TAG, "Error encountered during AESHelper.encrypt: " + e.getLocalizedMessage());
//                    }
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
        void selectLoginRegisterFragment(boolean showLoginFlag);
    }
}
