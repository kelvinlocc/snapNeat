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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.models.BaseResponse;
import com.accordhk.SnapNEat.models.ResponseFileUploadSettings;
import com.accordhk.SnapNEat.models.ResponseUserProfile;
import com.accordhk.SnapNEat.models.User;
import com.accordhk.SnapNEat.services.ApiWebServices;
import com.accordhk.SnapNEat.utils.Constants;
import com.accordhk.SnapNEat.utils.CustomFontButton;
import com.accordhk.SnapNEat.utils.CustomFontEditText;
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
 * {@link SettingsAccountFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsAccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsAccountFragment extends BaseFragment {
    private static String LOGGER_TAG = "SettingsAccountFragment";

    public static final int REQUEST_IMAGE_CAPTURE = 1888;
    public static final int PICK_IMAGE_REQUEST = 1;

    private PopupMenu popupMenu;

    private OnFragmentInteractionListener mListener;

    private Map<Integer, String> genders;

    private int glMaxTextureSize;

    ImageLoader mImageLoader;

    CircleImageView profile_pic;

    User user;

    String mCurrentPhotoPath;

    public SettingsAccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsAccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsAccountFragment newInstance(String param1, String param2) {
        SettingsAccountFragment fragment = new SettingsAccountFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        glMaxTextureSize = getGLMaxTextureSize();

        genders = new HashMap<>();
        genders.put(Constants.GENDER.MALE.getKey(), mUtils.getStringResource(R.string.common_male));
        genders.put(Constants.GENDER.FEMALE.getKey(), mUtils.getStringResource(R.string.common_female));
        genders.put(Constants.GENDER.UNSPECIFIED.getKey(), mUtils.getStringResource(R.string.common_unspecified));
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings_account, container, false);

        CustomFontTextView action_bar_title = (CustomFontTextView) view.findViewById(R.id.action_bar_title);
        action_bar_title.setText(mUtils.getStringResource(R.string.a7_my_account));

        final RelativeLayout rl_choose_photo = (RelativeLayout) view.findViewById(R.id.rl_choose_photo);
        final RelativeLayout rl_choose_gender = (RelativeLayout) view.findViewById(R.id.rl_choose_gender);

        ImageButton btn_back = (ImageButton) view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.goBack();
                }
            }
        });

        CustomFontTextView btn_submit = (CustomFontTextView) view.findViewById(R.id.btn_submit);
        btn_submit.setText(mUtils.getStringResource(R.string.a7_done));

        profile_pic = (CircleImageView) view.findViewById(R.id.profile_pic);

        user = new SharedPref(getContext()).getLoggedInUser();

        mImageLoader = VolleySingleton.getInstance(getContext()).getImageLoader();

        if(user != null) {
            final CustomFontEditText et_username = (CustomFontEditText) view.findViewById(R.id.et_username);
            et_username.setText(user.getUsername());

            final CustomFontTextView et_gender = (CustomFontTextView) view.findViewById(R.id.et_gender);
            et_gender.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    slideUp(rl_choose_gender);
                }
            });
            int gen = user.getGender();
            if(genders.containsKey(gen))
                et_gender.setText(genders.get(gen));

            CustomFontButton btn_gender_cancel = (CustomFontButton) view.findViewById(R.id.btn_gender_cancel);
            btn_gender_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupBackStack.pop();
                    slideDown(rl_choose_gender);
                }
            });

            CustomFontTextView tv_gender_unspecified = (CustomFontTextView) view.findViewById(R.id.tv_gender_unspecified);
            tv_gender_unspecified.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupBackStack.pop();
                    slideDown(rl_choose_gender);
                    et_gender.setText(mUtils.getStringResource(R.string.common_unspecified));
                }
            });

            CustomFontTextView tv_gender_male = (CustomFontTextView) view.findViewById(R.id.tv_gender_male);
            tv_gender_male.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupBackStack.pop();
                    slideDown(rl_choose_gender);
                    et_gender.setText(mUtils.getStringResource(R.string.common_male));
                }
            });

            CustomFontTextView tv_gender_female = (CustomFontTextView) view.findViewById(R.id.tv_gender_female);
            tv_gender_female.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupBackStack.pop();
                    slideDown(rl_choose_gender);
                    et_gender.setText(mUtils.getStringResource(R.string.common_female));
                }
            });

            final CustomFontEditText et_phone = (CustomFontEditText) view.findViewById(R.id.et_phone);
            et_phone.setText(user.getPhone());

            final CustomFontEditText et_email = (CustomFontEditText) view.findViewById(R.id.et_email);
            et_email.setText(user.getEmail());
            et_email.setClickable(false);

            final CustomFontEditText et_intro = (CustomFontEditText) view.findViewById(R.id.et_intro);
            et_intro.setText(user.getAbout());

            mImageLoader.get(user.getAvatarThumbnail(), ImageLoader.getImageListener(profile_pic, R.drawable.s1_bg_profile_pic, R.drawable.s1_bg_profile_pic));

            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String username = et_username.getText().toString().trim();
                    String email = et_email.getText().toString().trim();
                    String phone = et_phone.getText().toString().trim();
                    String gender = et_gender.getText().toString().trim();
                    String intro = et_intro.getText().toString().trim();

                    try {

                        Map<String, String> params = mUtils.getBaseRequestMap();
                        params.put(User.USERNAME, username);
                        params.put(User.EMAIL, email);

                        if (!phone.isEmpty())
                            params.put(User.PHONE, phone);

                        if (!gender.isEmpty()) {
                            int gen = -1;
                            if (genders.containsValue(gender)) {
                                for (int i = 0; i < genders.size(); i++) {
                                    if (gender.equals(genders.get(i))) {
                                        gen = i;
                                    }
                                }

                                params.put(User.GENDER, String.valueOf(gen));
                            }
                        }

                        if (!intro.isEmpty())
                            params.put(User.ABOUT, intro);

                        mProgressDialog.show();

                        Map<String, byte[]> multipartParams = new HashMap<String, byte[]>();

                        mApi.updateUserProfile(params, multipartParams, mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
                            @Override
                            public void onResponse(Object object) {
                                try {
                                    BaseResponse response = (BaseResponse) object;
                                    if (response != null) {
                                        if(response.getStatus() == Constants.RES_UNAUTHORIZED) {
                                            if(mListener != null) {
                                                mUtils.dismissDialog(mProgressDialog);
                                                mListener.showStartingFragmentFromLogout();
                                            }
                                        } else if (response.getStatus() != Constants.RES_SUCCESS) {
                                            mUtils.dismissDialog(mProgressDialog);
                                            mUtils.getErrorDialog(response.getMessage()).show();
                                        } else {

                                            // get user profile
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
                                                                    if (mListener != null) {
                                                                        mListener.showStartingFragmentFromLogout();
                                                                    }
                                                                } else if (responseUserProfile.getStatus() != Constants.RES_SUCCESS) {
                                                                    mUtils.getErrorDialog(responseUserProfile.getMessage()).show();
                                                                } else {
                                                                    new SharedPref(getContext()).setLoggedInUser(responseUserProfile.getUserInfo());
                                                                    if (mListener != null) {
                                                                        mListener.showSettingsFragment();
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
                            }
                        });
                    } catch (Exception e) {
                    }

                }
            });

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


            CustomFontButton btn_cancel = (CustomFontButton) view.findViewById(R.id.btn_cancel);
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupBackStack.pop();
                    slideDown(rl_choose_photo);
                }
            });

            final CustomFontTextView btn_change_avatar = (CustomFontTextView) view.findViewById(R.id.btn_change_avatar);
            btn_change_avatar.setOnClickListener(new View.OnClickListener() {
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

        }

        return view;
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(LOGGER_TAG, "requestCode: "+requestCode+"==resultCode: "+resultCode);

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
            // =======================3  END  =======================
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Log.d(LOGGER_TAG, "selected!!!");
            Uri uri = data.getData();

            Log.d(LOGGER_TAG, "Selected URI: "+uri.getPath());
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

    /**
     * Uploads selected avatar to server
     * @param
     */
//    private void uploadToServer(Bitmap bitmap) {
    private void uploadToServer(String imageFilePath) {
//        String base64string = mUtils.convertBitmapToBase64(bitmap);

        if(user != null) {
            try {
                Map<String, String> params = mUtils.getBaseRequestMap();

//                params.put(User.AVATAR, base64string);
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

                            if (response.getStatus() != Constants.RES_SUCCESS) {
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

                                                if (responseUserProfile != null) {
                                                    if (responseUserProfile.getStatus() != Constants.RES_SUCCESS) {
                                                        mUtils.dismissDialog(mProgressDialog);
                                                        mUtils.getErrorDialog(responseUserProfile.getMessage()).show();
                                                    } else {
                                                        new SharedPref(getContext()).setLoggedInUser(responseUserProfile.getUserInfo());

                                                        mImageLoader = VolleySingleton.getInstance(getContext()).getImageLoader();

                                                        mImageLoader.get(user.getAvatar(), ImageLoader.getImageListener(profile_pic, R.drawable.s1_bg_profile_pic, R.drawable.s1_bg_profile_pic));
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
                        mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_cannot_process_request)).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                mUtils.dismissDialog(mProgressDialog);
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
    public void onResume() {
        super.onResume();
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
        void showSettingsFragment();
        void goBack();
        void showStartingFragmentFromLogout();
    }
}
