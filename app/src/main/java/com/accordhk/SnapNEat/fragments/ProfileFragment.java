package com.accordhk.SnapNEat.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.models.BaseResponse;
import com.accordhk.SnapNEat.models.ResponseFileUploadSettings;
import com.accordhk.SnapNEat.models.ResponseFollowUser;
import com.accordhk.SnapNEat.models.ResponseUserProfile;
import com.accordhk.SnapNEat.models.User;
import com.accordhk.SnapNEat.services.ApiWebServices;
import com.accordhk.SnapNEat.utils.BlurredNetworkImageView;
import com.accordhk.SnapNEat.utils.Constants;
import com.accordhk.SnapNEat.utils.CustomFontButton;
import com.accordhk.SnapNEat.utils.CustomFontTextView;
import com.accordhk.SnapNEat.utils.SharedPref;
import com.accordhk.SnapNEat.utils.VolleySingleton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends BaseFragment {

    private static String LOGGER_TAG = "ProfileFragment";

    public static final int REQUEST_IMAGE_CAPTURE = 22888;
    public static final int PICK_IMAGE_REQUEST = 22;

    public static final String USER_ID = "userId";

    ImageLoader mImageLoader;

    Fragment mFragment;
    FragmentTransaction mTransaction;

    private int userId;

    private OnFragmentInteractionListener mListener;
    private View view;
    private Bundle mSavedInstanceState;

    private User user;
    private CircleImageView profile_pic;
    private BlurredNetworkImageView iv_profile_bg;

    private int glMaxTextureSize;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param u -> int userId
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(int u) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(USER_ID, u);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt(USER_ID);
        }

        glMaxTextureSize = getGLMaxTextureSize();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mSavedInstanceState = savedInstanceState;

        view = inflater.inflate(R.layout.fragment_profile, container, false);
        mUtils.dismissDialog(mProgressDialog);

        generateView();

        return view;
    }

    public void generateView() {
        profile_pic = (CircleImageView) view.findViewById(R.id.iv_profile_pic);
        iv_profile_bg = (BlurredNetworkImageView) view.findViewById(R.id.iv_profile_bg);
        final CustomFontTextView tv_username = (CustomFontTextView) view.findViewById(R.id.tv_username);
        final CustomFontTextView tv_no_posts = (CustomFontTextView) view.findViewById(R.id.tv_no_posts);
        final CustomFontTextView tv_no_followings = (CustomFontTextView) view.findViewById(R.id.tv_no_followings);
        final CustomFontTextView tv_no_followers = (CustomFontTextView) view.findViewById(R.id.tv_no_followers);
        final ImageButton btn_profile_action = (ImageButton) view.findViewById(R.id.btn_profile_action);
        final ImageButton btn_profile_menu = (ImageButton) view.findViewById(R.id.btn_profile_menu);

        final TextView tv_no_followings_label = (TextView) view.findViewById(R.id.tv_no_followings_label);

        final TextView tv_no_followers_label = (TextView) view.findViewById(R.id.tv_no_followers_label);

        final RelativeLayout rl_choose_photo = (RelativeLayout) view.findViewById(R.id.rl_choose_photo);
        final RelativeLayout rl_user_notif = (RelativeLayout) view.findViewById(R.id.rl_user_notif);
        final DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();

        CustomFontButton btn_cancel = (CustomFontButton) view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupBackStack.pop();
                slideDown(rl_choose_photo);
            }
        });

        CustomFontButton btn_notif_cancel = (CustomFontButton) view.findViewById(R.id.btn_notif_cancel);
        btn_notif_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupBackStack.pop();
                slideDown(rl_user_notif);
            }
        });

        final CustomFontTextView btn_turn_notif = (CustomFontTextView) view.findViewById(R.id.btn_turn_notif);

        CustomFontTextView settings_account_avatar_change_take_new_photo = (CustomFontTextView) view.findViewById(R.id.settings_account_avatar_change_take_new_photo);
        settings_account_avatar_change_take_new_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupBackStack.pop();
                slideDown(rl_choose_photo);
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                    }
                } else
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        });

        CustomFontTextView settings_account_avatar_change_choose_from_album = (CustomFontTextView) view.findViewById(R.id.settings_account_avatar_change_choose_from_album);
        settings_account_avatar_change_choose_from_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupBackStack.pop();
                slideDown(rl_choose_photo);
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent selIntent = new Intent();
                    // Show only images, no videos or anything else
                    selIntent.setType("image/*");
                    selIntent.setAction(Intent.ACTION_GET_CONTENT);
                    // Always show the chooser (if there are multiple options available)
                    startActivityForResult(Intent.createChooser(selIntent, "Select Picture"), PICK_IMAGE_REQUEST);
                } else
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        });

        final Button btn_tab1 = (Button) view.findViewById(R.id.btn_tab1);
        final Button btn_tab2 = (Button) view.findViewById(R.id.btn_tab2);
        final Button btn_tab3 = (Button) view.findViewById(R.id.btn_tab3);
        final Button btn_tab4 = (Button) view.findViewById(R.id.btn_tab4); //Footprints
        btn_tab1.setWidth(metrics.widthPixels/4);
        btn_tab2.setWidth(metrics.widthPixels/4);
        btn_tab3.setWidth(metrics.widthPixels/4);
        btn_tab4.setWidth(metrics.widthPixels/4);

        final Map<String, String> params = mUtils.getBaseRequestMap();
        params.put(User.USER_ID, String.valueOf(userId));

        try {
            mProgressDialog.show();
            mApi.getUserProfile(params, mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
                @Override
                public void onResponse(Object object) {
                    try {
                        Log.d(LOGGER_TAG, "getUserProfile: OK");

                        final ResponseUserProfile responseUserProfile = (ResponseUserProfile) object;
                        mUtils.dismissDialog(mProgressDialog);

                        if (responseUserProfile != null) {
                            /*if(responseUserProfile.getStatus() == Constants.RES_UNAUTHORIZED) {
                                if (mListener != null) {
                                    mListener.showStartingFragmentFromLogout();
                                }
                            } else */if (responseUserProfile.getStatus() != Constants.RES_SUCCESS) {
                                mUtils.getErrorDialog(responseUserProfile.getMessage()).show();
                                new SharedPref(getContext()).setLoggedInUser(null);
                                if (mListener != null)
                                    mListener.showHomeFragment();
                            } else {
                                user = responseUserProfile.getUserInfo();

                                ImageButton btn_back = (ImageButton) view.findViewById(R.id.btn_back);
                                btn_back.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (mListener != null) {
                                            mListener.goBack();
                                        }
                                    }
                                });

                                mImageLoader = VolleySingleton.getInstance(getContext()).getImageLoader();

                                Log.d(LOGGER_TAG, "avatar: " + responseUserProfile.getUserInfo().getAvatarThumbnail());
                                mImageLoader.get(responseUserProfile.getUserInfo().getAvatarThumbnail(), ImageLoader.getImageListener(profile_pic,
                                        R.drawable.s1_bg_profile_pic, R.drawable.s1_bg_profile_pic));

                                iv_profile_bg.setImageUrl(responseUserProfile.getUserInfo().getAvatar(), mImageLoader);
                                //                            mImageLoader.get(responseUserProfile.getUserInfo().getAvatar(), ImageLoader.getImageListener(iv_profile_bg, R.drawable.rounded_corner_image_view_bg, R.drawable.rounded_corner_image_view_bg));

                                Log.d(LOGGER_TAG, "username: " + responseUserProfile.getUserInfo().getUsername());
                                tv_username.setText(responseUserProfile.getUserInfo().getUsername());

                                Log.d(LOGGER_TAG, "no post: " + responseUserProfile.getTotalSnaps());
                                tv_no_posts.setText(String.valueOf(responseUserProfile.getTotalSnaps()));

                                Log.d(LOGGER_TAG, "no followings: " + responseUserProfile.getTotalFollowings());
                                tv_no_followings.setText(String.valueOf(responseUserProfile.getTotalFollowings()));

                                Log.d(LOGGER_TAG, "no followers: " + responseUserProfile.getTotalFollowers());
                                tv_no_followers.setText(String.valueOf(responseUserProfile.getTotalFollowers()));

                                tv_no_followings_label.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (mListener != null) {
                                            mListener.showUserFollowings(userId);
                                        }
                                    }
                                });

                                tv_no_followers_label.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (mListener != null) {
                                            mListener.showUserFollowers(userId);
                                        }
                                    }
                                });

                                // Check if user is currently logged in
                                final User currentUser = new SharedPref(getContext()).getLoggedInUser();

                                boolean isOtherUser = true;

                                if (currentUser != null) {
                                    if (currentUser.getId() == user.getId()) { // current user is equal to user in profile page

                                        isOtherUser = false;
                                        btn_profile_menu.setVisibility(View.INVISIBLE);

                                        profile_pic.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                SharedPref sharedPref = new SharedPref(getContext());
                                                if(sharedPref.getFileUploadSettingTypes().isEmpty()) {
                                                    try {
                                                        mApi.getFileUploadSetting(mUtils.getBaseRequestMap(), mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
                                                            @Override
                                                            public void onResponse(Object object) {
                                                                try {
                                                                    ResponseFileUploadSettings fileUploadSettings = (ResponseFileUploadSettings) object;

                                                                    if(fileUploadSettings.getStatus() == Constants.RES_UNAUTHORIZED) {
                                                                        if (mListener != null) {
                                                                            mListener.showStartingFragmentFromLogout();
                                                                        }
                                                                    } else if(fileUploadSettings.getStatus() == Constants.RES_SUCCESS) {
                                                                        (new SharedPref(getContext())).setFileUploadSettings(fileUploadSettings);
                                                                        slideUp(rl_choose_photo);
                                                                    }
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }

                                                            }

                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                error.printStackTrace();
                                                            }
                                                        });
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                } else {
                                                    slideUp(rl_choose_photo);
                                                }

                                            }
                                        });

                                        btn_profile_action.setImageResource(R.drawable.p1_btn_edit_profile);
                                        btn_profile_action.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                SharedPref sharedPref = new SharedPref(getContext());
                                                if(sharedPref.getFileUploadSettingTypes().isEmpty()) {
                                                    try {
                                                        mApi.getFileUploadSetting(mUtils.getBaseRequestMap(), mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
                                                            @Override
                                                            public void onResponse(Object object) {
                                                                try {
                                                                    ResponseFileUploadSettings fileUploadSettings = (ResponseFileUploadSettings) object;

                                                                    if(fileUploadSettings.getStatus() == Constants.RES_UNAUTHORIZED) {
                                                                        if (mListener != null) {
                                                                            mListener.showStartingFragmentFromLogout();
                                                                        }
                                                                    } else if(fileUploadSettings.getStatus() == Constants.RES_SUCCESS) {
                                                                        (new SharedPref(getContext())).setFileUploadSettings(fileUploadSettings);
                                                                        if (mListener != null) {
                                                                            mListener.addSnapPost();
                                                                        }
                                                                    }
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }

                                                            }

                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                error.printStackTrace();
                                                            }
                                                        });
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                } else {
                                                    if (mListener != null) {
                                                        mListener.addSnapPost();
                                                    }
                                                }

                                            }
                                        });

                                        btn_tab1.setWidth(metrics.widthPixels / 4);
                                        btn_tab1.setText(mUtils.getStringResource(R.string.p1_followings));
                                        btn_tab1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                btn_tab1.setBackgroundResource(android.R.color.white);
                                                btn_tab1.setTextColor(ContextCompat.getColor(getContext(), R.color.loginRegisterTabText));
                                                btn_tab2.setBackgroundResource(R.color.colorPrimary);
                                                btn_tab2.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
                                                btn_tab3.setBackgroundResource(R.color.colorPrimary);
                                                btn_tab3.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
                                                btn_tab4.setBackgroundResource(R.color.colorPrimary);
                                                btn_tab4.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
//                                                if (mListener != null) {
//                                                    mListener.showSnapsFollowings(userId);
//                                                }
                                                showSnapsFollowings(userId);
                                            }
                                        });

                                        btn_tab2.setWidth(metrics.widthPixels / 4);
                                        btn_tab2.setText(mUtils.getStringResource(R.string.p1_favourites));
                                        btn_tab2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                btn_tab2.setBackgroundResource(android.R.color.white);
                                                btn_tab2.setTextColor(ContextCompat.getColor(getContext(), R.color.loginRegisterTabText));
                                                btn_tab1.setBackgroundResource(R.color.colorPrimary);
                                                btn_tab1.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
                                                btn_tab3.setBackgroundResource(R.color.colorPrimary);
                                                btn_tab3.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
                                                btn_tab4.setBackgroundResource(R.color.colorPrimary);
                                                btn_tab4.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
//                                                if (mListener != null) {
//                                                    mListener.showSnapsFavourites(userId);
//                                                }
                                                showSnapsFavourites(userId);
                                            }
                                        });

                                        btn_tab3.setWidth(metrics.widthPixels / 4);
                                        btn_tab3.setVisibility(View.VISIBLE);
                                        btn_tab3.setText(mUtils.getStringResource(R.string.p1_gallery));
                                        btn_tab3.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                btn_tab3.setBackgroundResource(android.R.color.white);
                                                btn_tab3.setTextColor(ContextCompat.getColor(getContext(), R.color.loginRegisterTabText));
                                                btn_tab2.setBackgroundResource(R.color.colorPrimary);
                                                btn_tab2.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
                                                btn_tab1.setBackgroundResource(R.color.colorPrimary);
                                                btn_tab1.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
                                                btn_tab4.setBackgroundResource(R.color.colorPrimary);
                                                btn_tab4.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
//                                                if (mListener != null) {
//                                                    mListener.showGallery(userId);
//                                                }
                                                showGallery(userId);
                                            }
                                        });

                                        btn_tab4.setWidth(metrics.widthPixels / 4);
                                        btn_tab4.setVisibility(View.VISIBLE);
                                        btn_tab4.setText(mUtils.getStringResource(R.string.p1b_footprints));
                                        btn_tab4.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                btn_tab4.setBackgroundResource(android.R.color.white);
                                                btn_tab4.setTextColor(ContextCompat.getColor(getContext(), R.color.loginRegisterTabText));
                                                btn_tab2.setBackgroundResource(R.color.colorPrimary);
                                                btn_tab2.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
                                                btn_tab1.setBackgroundResource(R.color.colorPrimary);
                                                btn_tab1.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
                                                btn_tab3.setBackgroundResource(R.color.colorPrimary);
                                                btn_tab3.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
//                                                if (mListener != null) {
//                                                    mListener.showGallery(userId);
//                                                }
                                                showFootprints(userId);
                                            }
                                        });

                                    } else { // other user profile

                                        btn_profile_menu.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (user.getNotificationFlag() == Constants.FLAG_TRUE) {
                                                    btn_turn_notif.setText(mUtils.getStringResource(R.string.p1c_turn_off_notifications));
                                                } else {
                                                    btn_turn_notif.setText(mUtils.getStringResource(R.string.p1c_turn_on_notifications));
                                                }
                                                slideUp(rl_user_notif);
                                            }
                                        });

                                        btn_turn_notif.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mPopupBackStack.pop();
                                                slideDown(rl_user_notif);

                                                Map<String, String> notifParam = mUtils.getBaseRequestMap();
                                                notifParam.put(User.USER_ID, String.valueOf(user.getId()));
                                                if (user.getNotificationFlag() == Constants.FLAG_TRUE)
                                                    notifParam.put(User.ALLOW_NOTIF, String.valueOf(Constants.FLAG_FALSE));
                                                else
                                                    notifParam.put(User.ALLOW_NOTIF, String.valueOf(Constants.FLAG_TRUE));

                                                mProgressDialog.show();
                                                try {
                                                    mApi.updateNotification(notifParam, mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
                                                        @Override
                                                        public void onResponse(Object object) {
                                                            try {
                                                                mUtils.dismissDialog(mProgressDialog);
                                                                BaseResponse response = (BaseResponse) object;

                                                                if(response.getStatus() == Constants.RES_UNAUTHORIZED) {
                                                                    if(mListener != null) {
                                                                        mListener.showStartingFragmentFromLogout();
                                                                    }
                                                                } else if (response.getStatus() != Constants.RES_SUCCESS) {
                                                                    mUtils.getErrorDialog(response.getMessage()).show();
                                                                } else {
                                                                    if(user.getNotificationFlag() == Constants.FLAG_TRUE)
                                                                        user.setNotificationFlag(Constants.FLAG_FALSE);
                                                                    else
                                                                        user.setNotificationFlag(Constants.FLAG_TRUE);
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
                                            }
                                        });
                                        if (responseUserProfile.getUserInfo().getFollowFlag() == Constants.FLAG_TRUE) {
                                            btn_profile_action.setImageResource(R.drawable.s7_btn_add_user_sel);
                                        } else {
                                            btn_profile_action.setImageResource(R.drawable.s7_btn_add_user);
//                                            btn_profile_action.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//                                                    final View btnView = v;
//                                                    try {
//                                                        Map<String, String> params = mUtils.getBaseRequestMap();
//                                                        params.put(User.USER_ID, String.valueOf(userId));
//
//                                                        mProgressDialog.show();
//                                                        mApi.postFollowUser(params, mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
//                                                            @Override
//                                                            public void onResponse(Object object) {
//                                                                try {
//                                                                    ResponseFollowUser response = (ResponseFollowUser) object;
//                                                                    mUtils.dismissDialog(mProgressDialog);
//
//                                                                    if(response.getStatus() == Constants.RES_UNAUTHORIZED) {
//                                                                        if(mListener != null) {
//                                                                            mListener.showStartingFragmentFromLogout();
//                                                                        }
//                                                                    } else if (response.getStatus() != Constants.RES_SUCCESS) {
//                                                                        mUtils.getErrorDialog(response.getMessage()).show();
//                                                                    } else {
//                                                                        btn_profile_action.setImageResource(R.drawable.s7_btn_add_user_sel);
//                                                                        ((CustomFontTextView) view.findViewById(R.id.tv_no_followers)).setText(String.valueOf(response.getTotalFollowers()));
//
//                                                                        btn_profile_menu.setVisibility(View.VISIBLE);
//                                                                    }
//                                                                } catch (Exception e) {
//                                                                    e.printStackTrace();
//                                                                    mUtils.dismissDialog(mProgressDialog);
//                                                                }
//                                                            }
//
//                                                            @Override
//                                                            public void onErrorResponse(VolleyError error) {
//                                                                error.printStackTrace();
//                                                                mUtils.dismissDialog(mProgressDialog);
//                                                                mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_cannot_process_request)).show();
//                                                            }
//                                                        });
//                                                    } catch (Exception e) {
//                                                        e.printStackTrace();
//                                                        mUtils.dismissDialog(mProgressDialog);
//                                                    }
//                                                }
//                                            });
                                        }

                                        btn_profile_action.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                try {
                                                    Map<String, String> params = mUtils.getBaseRequestMap();
                                                    params.put(User.USER_ID, String.valueOf(userId));

                                                    mProgressDialog.show();
                                                    mApi.postFollowUser(params, mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
                                                        @Override
                                                        public void onResponse(Object object) {
                                                            try {
                                                                ResponseFollowUser response = (ResponseFollowUser) object;
                                                                mUtils.dismissDialog(mProgressDialog);

                                                                if(response.getStatus() == Constants.RES_UNAUTHORIZED) {
                                                                    if(mListener != null) {
                                                                        mListener.showStartingFragmentFromLogout();
                                                                    }
                                                                } else if (response.getStatus() != Constants.RES_SUCCESS) {
                                                                    mUtils.getErrorDialog(response.getMessage()).show();
                                                                } else {
                                                                    if(response.getFollowFlag() == Constants.FLAG_TRUE) {
                                                                        btn_profile_action.setImageResource(R.drawable.s7_btn_add_user_sel);
                                                                        btn_profile_menu.setVisibility(View.INVISIBLE);
                                                                    } else {
                                                                        btn_profile_action.setImageResource(R.drawable.s7_btn_add_user);
                                                                    }
                                                                    ((CustomFontTextView) view.findViewById(R.id.tv_no_followers)).setText(String.valueOf(response.getTotalFollowers()));
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
                                        });

                                    }


                                }

                                if (isOtherUser) {
                                    btn_tab1.setWidth(metrics.widthPixels / 2);
                                    btn_tab1.setText(mUtils.getStringResource(R.string.p1b_footprints));
                                    btn_tab1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            btn_tab1.setBackgroundResource(android.R.color.white);
                                            btn_tab1.setTextColor(ContextCompat.getColor(getContext(), R.color.loginRegisterTabText));
                                            btn_tab2.setBackgroundResource(R.color.colorPrimary);
                                            btn_tab2.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
//                                            if (mListener != null) {
//                                                mListener.showFootprints(userId);
//                                            }
                                            showFootprints(userId);
                                        }
                                    });

                                    btn_tab2.setWidth(metrics.widthPixels / 2);
                                    btn_tab2.setText(mUtils.getStringResource(R.string.p1_gallery));
                                    btn_tab2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            btn_tab2.setBackgroundResource(android.R.color.white);
                                            btn_tab2.setTextColor(ContextCompat.getColor(getContext(), R.color.loginRegisterTabText));
                                            btn_tab1.setBackgroundResource(R.color.colorPrimary);
                                            btn_tab1.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
//                                            if (mListener != null) {
//                                                mListener.showGallery(userId);
//                                            }
                                            showGallery(userId);
                                        }
                                    });

                                    btn_tab3.setVisibility(View.INVISIBLE);
                                    btn_tab4.setVisibility(View.INVISIBLE);

//                                    if (user.getFollowFlag() == Constants.FLAG_TRUE)
//                                        btn_profile_menu.setVisibility(View.VISIBLE);
//                                    else
//                                        btn_profile_menu.setVisibility(View.INVISIBLE);
                                }

                                if (view.findViewById(R.id.profile_frame_content) != null) {

                                    Log.d(LOGGER_TAG, "PROFILE FRAME CONTENT IS NOT NULL");
                                    if (mSavedInstanceState == null) {
                                        Log.d(LOGGER_TAG, "mSavedInstanceState IS NULL");
                                        btn_tab1.setBackgroundResource(android.R.color.white);
                                        btn_tab1.setTextColor(ContextCompat.getColor(getContext(), R.color.loginRegisterTabText));

                                        //mTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                        mTransaction = getChildFragmentManager().beginTransaction();

                                        Bundle args = new Bundle();

                                        if (isOtherUser) {
                                            args.putInt(ProfileFootprintsFragment.USER_ID, userId);
                                            mFragment = new ProfileFootprintsFragment();
                                        } else {
                                            args.putInt(ProfileFollowingsFragment.USER_ID, userId);
                                            mFragment = new ProfileFollowingsFragment();
                                        }
                                        mFragment.setArguments(args);

                                        mTransaction.replace(R.id.profile_frame_content, mFragment);
                                        mTransaction.commit();
                                    } else {
                                        Log.d(LOGGER_TAG, "mSavedInstanceState IS NOT NULL");
                                    }

                                } else {
                                    Log.d(LOGGER_TAG, "PROFILE FRAME CONTENT IS NULL");
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

        mUtils.dismissDialog(mProgressDialog);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(LOGGER_TAG, "requestCode: "+requestCode+"==resultCode: "+resultCode);

        if (requestCode == ProfileFootprintsFragment.REQUEST_CHECK_SETTINGS && resultCode == PackageManager.PERMISSION_GRANTED) {
            Fragment fragment = getChildFragmentManager().findFragmentById(R.id.profile_frame_content);
            if(fragment instanceof ProfileFootprintsFragment) {
                Log.d(LOGGER_TAG, "Showing ProfileFootprintsFragment");
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && data != null) {
            Log.d(LOGGER_TAG, "captured!!!");
            Bundle extras = data.getExtras();
            Map<String, Object> bitmapResult = mUtils.processBitmap(getActivity(), (Bitmap) extras.get("data"), glMaxTextureSize);
            String error = mUtils.validateUploadImage(bitmapResult);

            if(error.isEmpty())
//                uploadToServer((Bitmap) bitmapResult.get(Constants.PHOTO_BITMAP));
                uploadToServer(bitmapResult.get(Constants.PHOTO_PATH).toString());
            else
                mUtils.getErrorDialog(error).show();
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Log.d(LOGGER_TAG, "selected!!!");
            Uri uri = data.getData();

            Log.d(LOGGER_TAG, "Selected URI: " + uri.getPath());
            try {
                Map<String, Object> bitmapResult = mUtils.processBitmap(getActivity(), MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri), glMaxTextureSize);
                String error = mUtils.validateUploadImage(bitmapResult);

                if(error.isEmpty())
//                    uploadToServer((Bitmap) bitmapResult.get(Constants.PHOTO_BITMAP));
                    uploadToServer(bitmapResult.get(Constants.PHOTO_PATH).toString());
                else
                    mUtils.getErrorDialog(error).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadToServer(String imageFilePath) {
        if(user != null) {
            try {
                Map<String, String> params = mUtils.getBaseRequestMap();
                params.put(User.FILE_TYPE, Constants.FILE_TYPE_JPG);

                mProgressDialog.show();
                Uri uri = Uri.parse(imageFilePath);
                Map<String, byte[]> multiPartParams = new HashMap<>();
                multiPartParams.put(uri.toString().trim(), mUtils.converUriToFileData(getActivity(), uri));

                mApi.updateUserProfile(params, multiPartParams, mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
                    @Override
                    public void onResponse(Object object) {
                        try {
                            BaseResponse response = (BaseResponse) object;

                            if(response.getStatus() == Constants.RES_UNAUTHORIZED) {
                                if(mListener != null) {
                                    mUtils.dismissDialog(mProgressDialog);
                                    mListener.showStartingFragmentFromLogout();
                                }
                            } else if (response.getStatus() != Constants.RES_SUCCESS) {
                                mUtils.dismissDialog(mProgressDialog);
                                mUtils.getErrorDialog(response.getMessage()).show();
                            } else {
                                try {

                                    Map<String, String> userProfileParam = mUtils.getBaseRequestMap();
                                    userProfileParam.put(User.USER_ID, String.valueOf(user.getId()));

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
                                                        new SharedPref(getContext()).setLoggedInUser(responseUserProfile.getUserInfo());

                                                        mImageLoader = VolleySingleton.getInstance(getContext()).getImageLoader();

                                                        mImageLoader.get(responseUserProfile.getUserInfo().getAvatar(), ImageLoader.getImageListener(profile_pic, R.drawable.s1_bg_profile_pic, R.drawable.s1_bg_profile_pic));
                                                        iv_profile_bg.setImageUrl(responseUserProfile.getUserInfo().getAvatar(), mImageLoader);
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
                                    mUtils.dismissDialog(mProgressDialog);
                                    e.printStackTrace();
                                }
                            }
                            mUtils.dismissDialog(mProgressDialog);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mUtils.dismissDialog(mProgressDialog);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                mUtils.dismissDialog(mProgressDialog);
            }
        }
    }

//    /**
//     * Uploads selected avatar to server
//     * @param bitmap
//     */
//    private void uploadToServer(Bitmap bitmap) {
//        String base64string = mUtils.convertBitmapToBase64(bitmap);
//
//        if(user != null) {
//            try {
//                Map<String, String> params = mUtils.getBaseRequestMap();
//
//                Map<String, String> bodyParams = new HashMap<String, String>();
//                params.put(User.AVATAR, base64string);
//                params.put(User.FILE_TYPE, Constants.FILE_TYPE_JPG);
//
//                mProgressDialog.show();
//                mApi.updateUserProfile(params, mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
//                    @Override
//                    public void onResponse(Object object) {
//                        try {
//                            BaseResponse response = (BaseResponse) object;
//
//                            if(response.getStatus() == Constants.RES_UNAUTHORIZED) {
//                                if(mListener != null) {
//                                    mUtils.dismissDialog(mProgressDialog);
//                                    mListener.showStartingFragmentFromLogout();
//                                }
//                            } else if (response.getStatus() != Constants.RES_SUCCESS) {
//                                mUtils.dismissDialog(mProgressDialog);
//                                mUtils.getErrorDialog(response.getMessage()).show();
//                            } else {
//                                try {
//
//                                    Map<String, String> userProfileParam = mUtils.getBaseRequestMap();
//                                    userProfileParam.put(User.USER_ID, String.valueOf(user.getId()));
//
//                                    mProgressDialog.show();
//                                    mApi.getUserProfile(userProfileParam, mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
//                                        @Override
//                                        public void onResponse(Object object) {
//                                            try {
//                                                ResponseUserProfile responseUserProfile = (ResponseUserProfile) object;
//
//                                                mUtils.dismissDialog(mProgressDialog);
//
//                                                if (responseUserProfile != null) {
//                                                    if(responseUserProfile.getStatus() == Constants.RES_UNAUTHORIZED) {
//                                                        if(mListener != null) {
//                                                            mListener.showStartingFragmentFromLogout();
//                                                        }
//                                                    } else if (responseUserProfile.getStatus() != Constants.RES_SUCCESS) {
//                                                        mUtils.getErrorDialog(responseUserProfile.getMessage()).show();
//                                                    } else {
//                                                        new SharedPref(getContext()).setLoggedInUser(responseUserProfile.getUserInfo());
//
//                                                        mImageLoader = VolleySingleton.getInstance(getContext()).getImageLoader();
//
//                                                        mImageLoader.get(responseUserProfile.getUserInfo().getAvatar(), ImageLoader.getImageListener(profile_pic, R.drawable.s1_bg_profile_pic, R.drawable.s1_bg_profile_pic));
//                                                        iv_profile_bg.setImageUrl(responseUserProfile.getUserInfo().getAvatar(), mImageLoader);
//                                                    }
//                                                }
//
//                                            } catch (Exception e) {
//                                                e.printStackTrace();
//                                                mUtils.dismissDialog(mProgressDialog);
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onErrorResponse(VolleyError error) {
//                                            error.printStackTrace();
//                                            mUtils.dismissDialog(mProgressDialog);
//                                            mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_cannot_process_request)).show();
//                                        }
//                                    });
//                                } catch (Exception e) {
//                                    mUtils.dismissDialog(mProgressDialog);
//                                    e.printStackTrace();
//                                }
//                            }
//                            mUtils.dismissDialog(mProgressDialog);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                        mUtils.dismissDialog(mProgressDialog);
//                    }
//                });
//            } catch (Exception e) {
//                e.printStackTrace();
//                mUtils.dismissDialog(mProgressDialog);
//            }
//        }
//    }

    public void showSnapsFollowings(int userId) {
        mTransaction = getChildFragmentManager().beginTransaction();

        Bundle args = new Bundle();
        args.putInt(ProfileFollowingsFragment.USER_ID, userId);
        ProfileFollowingsFragment fragment = new ProfileFollowingsFragment();
        fragment.setArguments(args);

        mTransaction.replace(R.id.profile_frame_content, fragment);
        mTransaction.commit();
    }

    public void showSnapsFavourites(int userId) {
        mTransaction = getChildFragmentManager().beginTransaction();

        Bundle args = new Bundle();
        args.putInt(ProfileFavouritesFragment.USER_ID, userId);
        ProfileFavouritesFragment fragment = new ProfileFavouritesFragment();
        fragment.setArguments(args);

        mTransaction.replace(R.id.profile_frame_content, fragment);
        mTransaction.commit();
    }

    public void showFootprints(int userId) {
        mTransaction = getChildFragmentManager().beginTransaction();

        Bundle args = new Bundle();
        args.putInt(ProfileFootprintsFragment.USER_ID, userId);
        ProfileFootprintsFragment fragment = new ProfileFootprintsFragment();
        fragment.setArguments(args);

        mTransaction.replace(R.id.profile_frame_content, fragment);
        mTransaction.commit();
    }

    public void showGallery(int userId) {
        mTransaction = getChildFragmentManager().beginTransaction();

        Bundle args = new Bundle();
        args.putInt(ProfileGalleryFragment.USER_ID, userId);
        ProfileGalleryFragment fragment = new ProfileGalleryFragment();
        fragment.setArguments(args);

        mTransaction.replace(R.id.profile_frame_content, fragment);
        mTransaction.commit();
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
                break;
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
        void addSnapPost();
        void showUserFollowings(int userId);
        void showUserFollowers(int userId);
        void showHomeFragment();
        void goBack();
        void showStartingFragmentFromLogout();
    }
}
