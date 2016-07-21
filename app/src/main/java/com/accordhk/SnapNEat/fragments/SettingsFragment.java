package com.accordhk.SnapNEat.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.models.BaseResponse;
import com.accordhk.SnapNEat.models.HotSearch;
import com.accordhk.SnapNEat.models.Restaurant;
import com.accordhk.SnapNEat.models.User;
import com.accordhk.SnapNEat.services.ApiWebServices;
import com.accordhk.SnapNEat.utils.Constants;
import com.accordhk.SnapNEat.utils.CustomFontTextView;
import com.accordhk.SnapNEat.utils.SharedPref;
import com.accordhk.SnapNEat.utils.VolleySingleton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        mUtils.dismissDialog(mProgressDialog);

        RelativeLayout rl_logged_in = (RelativeLayout) view.findViewById(R.id.rl_logged_in);
        RelativeLayout rl_guest = (RelativeLayout) view.findViewById(R.id.rl_guest);
        LinearLayout ll_password_row = (LinearLayout) view.findViewById(R.id.ll_password_row);

        CustomFontTextView action_bar_title = (CustomFontTextView) view.findViewById(R.id.action_bar_title);
        action_bar_title.setText(mUtils.getStringResource(R.string.a6_settings));

        ImageButton btn_back = (ImageButton) view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.goBack();
                }
            }
        });

        // Check if user is currently logged in
        User user = new SharedPref(getContext()).getLoggedInUser();
        if(user != null) {
            rl_logged_in.setVisibility(View.VISIBLE);
            rl_guest.setVisibility(View.INVISIBLE);

            if(new SharedPref(getContext()).getIsFacebookUser() == true)
                ll_password_row.setVisibility(View.GONE);

            LinearLayout ll_my_account = (LinearLayout) view.findViewById(R.id.ll_my_account);
            ll_my_account.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.showSettingsAccountFragment();
                    }
                }
            });
            ImageButton btn_my_account = (ImageButton) view.findViewById(R.id.btn_my_account);
            btn_my_account.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null) {
                        mListener.showSettingsAccountFragment();
                    }
                }
            });

            LinearLayout ll_connected_services = (LinearLayout) view.findViewById(R.id.ll_connected_services);
            ll_connected_services.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null) {
                        mListener.showSettingsConnectedServicesFragment();
                    }
                }
            });
            ImageButton btn_con_services = (ImageButton) view.findViewById(R.id.btn_con_services);
            btn_con_services.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null) {
                        mListener.showSettingsConnectedServicesFragment();
                    }
                }
            });

            LinearLayout ll_language1 = (LinearLayout) view.findViewById(R.id.ll_language1);
            ll_language1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null) {
                        mListener.showLanguageFragment();
                    }
                }
            });
            ImageButton btn_language1 = (ImageButton) view.findViewById(R.id.btn_language1);
            btn_language1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null) {
                        mListener.showLanguageFragment();
                    }
                }
            });

            LinearLayout ll_about1 = (LinearLayout) view.findViewById(R.id.ll_about1);
            ll_about1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null) {
                        mListener.showAboutFragment();
                    }
                }
            });
            ImageButton btn_about1 = (ImageButton) view.findViewById(R.id.btn_about1);
            btn_about1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null) {
                        mListener.showAboutFragment();
                    }
                }
            });

            if(new SharedPref(getContext()).getIsFacebookUser() != true) {
                ll_password_row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mListener != null) {
                            mListener.showSettingsChangePasswordFragment();
                        }
                    }
                });
                ImageButton btn_change_pass = (ImageButton) view.findViewById(R.id.btn_change_pass);
                btn_change_pass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mListener != null) {
                            mListener.showSettingsChangePasswordFragment();
                        }
                    }
                });

            } else {
                ll_password_row.setVisibility(View.INVISIBLE);
            }

            Button btn_logout = (Button) view.findViewById(R.id.btn_logout);
            btn_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        Map<String, String> params = mUtils.getBaseRequestMap();
                        params.put(User.UDID, Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID));

                        mProgressDialog.show();
                        mApi.logoutUser(params, mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
                            @Override
                            public void onResponse(Object object) {

                                try {
                                    BaseResponse response = (BaseResponse) object;
                                    mUtils.dismissDialog(mProgressDialog);

                                    if(mListener != null) {
                                        mListener.showHomeFragmentFromLogout();
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
                        mUtils.dismissDialog(mProgressDialog);
                        e.printStackTrace();
                    }
                }
            });

        } else {
            rl_logged_in.setVisibility(View.INVISIBLE);
            rl_guest.setVisibility(View.VISIBLE);

            LinearLayout ll_language2 = (LinearLayout) view.findViewById(R.id.ll_language2);
            ll_language2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.showLanguageFragment();
                    }
                }
            });
            ImageButton btn_language2 = (ImageButton) view.findViewById(R.id.btn_language2);
            btn_language2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.showLanguageFragment();
                    }
                }
            });

            LinearLayout ll_about2 = (LinearLayout) view.findViewById(R.id.ll_about2);
            ll_about2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.showAboutFragment();
                    }
                }
            });
            ImageButton btn_about2 = (ImageButton) view.findViewById(R.id.btn_about2);
            btn_about2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.showAboutFragment();
                    }
                }
            });

            Button btn_login = (Button) view.findViewById(R.id.btn_login);
            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null) {
                        mListener.showLoginRegistrationFragment(true);
                    }
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

    @Override
    public void onPause() {
        super.onPause();
        VolleySingleton.getInstance(getContext()).getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
//        mListener = null;
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
        void showSettingsAccountFragment();
        void showSettingsConnectedServicesFragment();
        void showLanguageFragment();
        void showAboutFragment();
        void showSettingsChangePasswordFragment();
        void showHomeFragmentFromLogout();
        void showLoginRegistrationFragment(boolean showLoginFlag);
        void updateNavigationDrawerItems();
        void goBack();
    }
}
