package com.accordhk.SnapNEat.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.adapters.PostNewSnapRecyclerViewAdapter;
import com.accordhk.SnapNEat.dao.HotSearchFilterDataSource;
import com.accordhk.SnapNEat.models.HotSearch;
import com.accordhk.SnapNEat.models.ResponseBaseWithId;
import com.accordhk.SnapNEat.models.Restaurant;
import com.accordhk.SnapNEat.models.Snap;
import com.accordhk.SnapNEat.models.Spending;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PostSnapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PostSnapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostSnapFragment extends BaseFragment {
    private static String LOGGER_TAG = "PostSnapFragment";

    public static final int REQUEST_IMAGE_CAPTURE = 12888;
    public static final int PICK_IMAGE_REQUEST = 12;

    private OnFragmentInteractionListener mListener;

    public PostSnapFragment() {
        // Required empty public constructor
    }

    private int rating;

    private int glMaxTextureSize;

    private List<String> bitmaps; //for selected photos saved in preference
    private RecyclerView rv_snap_photos;
    private PostNewSnapRecyclerViewAdapter adapter;

    String mCurrentPhotoPath;

    CustomFontTextView tv_snap_resto;
    CustomFontTextView tv_snap_location;
    CustomFontTextView tv_snap_spending;
    CustomFontTextView tv_snap_dish;

    Restaurant restaurant;
    List<String> dish;
    List<String> spendingList;
    Spending spending;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostSnapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostSnapFragment newInstance(String param1, String param2) {
        PostSnapFragment fragment = new PostSnapFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOGGER_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        glMaxTextureSize = getGLMaxTextureSize();

        restaurant = new Restaurant();
        dish = new ArrayList<String>();
        spendingList = new ArrayList<String>();
        rating = 5;

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bitmaps = new ArrayList<String>();

        Log.d(LOGGER_TAG, "onCreateView");

        final View view = inflater.inflate(R.layout.fragment_post_snap, container, false);
        final RelativeLayout rl_choose_photo = (RelativeLayout) view.findViewById(R.id.rl_choose_photo);

        LinearLayout rl_resto = (LinearLayout) view.findViewById(R.id.rl_resto);
        rl_resto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) {
                    mListener.showRestaurantList(restaurant);
                }
            }
        });

        ImageButton ib_resto = (ImageButton) view.findViewById(R.id.ib_resto);
        ib_resto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) {
                    mListener.showRestaurantList(restaurant);
                }
            }
        });

        LinearLayout rl_spending = (LinearLayout) view.findViewById(R.id.rl_spending);
        rl_spending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) {
                    mListener.showHotSearchMore(spendingList, HotSearch.Category.SPENDINGS.getKey(), true, false);
                }
            }
        });

        ImageButton ib_spending = (ImageButton) view.findViewById(R.id.ib_spending);
        ib_spending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) {
                    mListener.showHotSearchMore(spendingList, HotSearch.Category.SPENDINGS.getKey(), true, false);
                }
            }
        });

        LinearLayout rl_dish = (LinearLayout) view.findViewById(R.id.rl_dish);
        rl_dish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) {
                    mListener.showHotSearchMoreShowAll(dish, HotSearch.Category.DISH.getKey(), false, false, false);
                }
            }
        });

        ImageButton ib_dish = (ImageButton) view.findViewById(R.id.ib_dish);
        ib_dish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) {
                    mListener.showHotSearchMoreShowAll(dish, HotSearch.Category.DISH.getKey(), false, false, false);
                }
            }
        });

        final CustomFontEditText tv_snap_title = (CustomFontEditText) view.findViewById(R.id.tv_snap_title);
        final CustomFontEditText tv_snap_comments = (CustomFontEditText) view.findViewById(R.id.tv_snap_comments);
        final CustomFontEditText tv_snap_hashtags = (CustomFontEditText) view.findViewById(R.id.tv_snap_hashtags);

        tv_snap_resto = (CustomFontTextView) view.findViewById(R.id.tv_snap_resto);
        tv_snap_location = (CustomFontTextView) view.findViewById(R.id.tv_snap_location);
        tv_snap_spending = (CustomFontTextView) view.findViewById(R.id.tv_snap_spending);
        tv_snap_dish = (CustomFontTextView) view.findViewById(R.id.tv_snap_dish);

        final ImageButton ib_rating1 = (ImageButton) view.findViewById(R.id.ib_rating1);
        final ImageButton ib_rating2 = (ImageButton) view.findViewById(R.id.ib_rating2);
        final ImageButton ib_rating3 = (ImageButton) view.findViewById(R.id.ib_rating3);
        final ImageButton ib_rating4 = (ImageButton) view.findViewById(R.id.ib_rating4);
        final ImageButton ib_rating5 = (ImageButton) view.findViewById(R.id.ib_rating5);

        ImageButton btn_back = (ImageButton) view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(mUtils.getStringResource(R.string.leave_page));

                builder.setPositiveButton(mContext.getResources().getString(R.string.common_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if (mListener != null) {
                            mUtils.resetSearchSettings();
                            mListener.goBack();
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
        });

        CustomFontTextView action_bar_title = (CustomFontTextView) view.findViewById(R.id.action_bar_title);
        action_bar_title.setText(mUtils.getStringResource(R.string.s12_post));

        CustomFontTextView btn_submit = (CustomFontTextView) view.findViewById(R.id.btn_submit);
        btn_submit.setText(mUtils.getStringResource(R.string.s12_submit));
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.show();

                String error = "";

                // check photos
                if (bitmaps.size() < 1)
                    error = mUtils.getStringResource(R.string.error_minimum_no_images);
                else {
                    if(bitmaps.size() > 5)
                        error = mUtils.getStringResource(R.string.error_maximum_no_images);
                }

                if (error.isEmpty() == false) {
                    mUtils.dismissDialog(mProgressDialog);
                    mUtils.getErrorDialog(error).show();
                } else {

                    try {

                        // get filetypes
                        List<String> filetypes = new ArrayList<String>();
                        List<String> dishes = dish;

                        Map<String, byte[]> multiPartParams = new HashMap<String, byte[]>();

                        for (int i = 0; i < bitmaps.size(); i++) {
                            Uri uri = Uri.parse(bitmaps.get(i));
                            Log.d(LOGGER_TAG, "bitmap uri: "+uri.toString());

                            multiPartParams.put(uri.toString().trim(), mUtils.converUriToFileData(getActivity(), uri));

                            // get extension
                            String ext = "";
//                            String extTemp = mUtils.getImagePathFromURI(getActivity(), uri);
                            String extTemp = uri.toString();
                            if (extTemp.contains(".")) {
                                ext = extTemp.substring(extTemp.lastIndexOf(".") + 1);
                                if (ext.equalsIgnoreCase("jpg"))
                                    filetypes.add(Constants.FILE_TYPE_JPG);
                                else
                                    filetypes.add(ext);
                            }
                        }

                        Map<String, List<String>> bodyParams = new HashMap<String, List<String>>();
                        bodyParams.put(Snap.FILE_TYPES, filetypes);
                        bodyParams.put(Snap.DISH, dishes);

                        Map<String, String> params = mUtils.getBaseRequestMap();
                        params.put(Snap.RATING, String.valueOf(rating));
                        params.put(Snap.TITLE, String.valueOf(tv_snap_title.getText()));
                        if (restaurant != null)
                            params.put(Snap.RESTAURANT_ID, String.valueOf(restaurant.getId()));
                        if (spending != null)
                            params.put(Snap.SPENDING_ID, String.valueOf(spending.getId()));

                        params.put(Snap.COMMENTS, tv_snap_comments.getText().toString().trim());
                        params.put(Snap.HASHTAGS, tv_snap_hashtags.getText().toString().trim());
                        mUtils.dismissDialog(mProgressDialog);

                        mProgressDialog.show();

                        mApi.postNewSnap(params, bodyParams, multiPartParams, mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
                            @Override
                            public void onResponse(Object object) {
                                try {
                                    ResponseBaseWithId response = (ResponseBaseWithId) object;

                                    mUtils.dismissDialog(mProgressDialog);

                                    if (response != null) {

                                        if(response.getStatus() == Constants.RES_UNAUTHORIZED) {
                                            if (mListener != null) {
                                                mListener.showStartingFragmentFromLogout();
                                            }

                                        } else if (response.getStatus() != Constants.RES_SUCCESS) {
                                            mUtils.getErrorDialog(response.getMessage()).show();
                                        } else {

                                            if (mListener != null) {
                                                getActivity().getSupportFragmentManager().popBackStack();
                                                User user = new SharedPref(mContext).getLoggedInUser();
                                                mListener.showUserProfile(user.getId());
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
                                error.printStackTrace();
                                mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_cannot_process_request)).show();
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        mUtils.dismissDialog(mProgressDialog);
                    }
                }

            }
        });

        ib_rating1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = 1;
                ib_rating1.setImageResource(R.drawable.s1_rating_star_sel);
                ib_rating2.setImageResource(R.drawable.s1_rating_star_def);
                ib_rating3.setImageResource(R.drawable.s1_rating_star_def);
                ib_rating4.setImageResource(R.drawable.s1_rating_star_def);
                ib_rating5.setImageResource(R.drawable.s1_rating_star_def);
            }
        });

        ib_rating2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = 2;
                ib_rating1.setImageResource(R.drawable.s1_rating_star_sel);
                ib_rating2.setImageResource(R.drawable.s1_rating_star_sel);
                ib_rating3.setImageResource(R.drawable.s1_rating_star_def);
                ib_rating4.setImageResource(R.drawable.s1_rating_star_def);
                ib_rating5.setImageResource(R.drawable.s1_rating_star_def);
            }
        });

        ib_rating3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = 3;
                ib_rating1.setImageResource(R.drawable.s1_rating_star_sel);
                ib_rating2.setImageResource(R.drawable.s1_rating_star_sel);
                ib_rating3.setImageResource(R.drawable.s1_rating_star_sel);
                ib_rating4.setImageResource(R.drawable.s1_rating_star_def);
                ib_rating5.setImageResource(R.drawable.s1_rating_star_def);
            }
        });

        ib_rating4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = 4;
                ib_rating1.setImageResource(R.drawable.s1_rating_star_sel);
                ib_rating2.setImageResource(R.drawable.s1_rating_star_sel);
                ib_rating3.setImageResource(R.drawable.s1_rating_star_sel);
                ib_rating4.setImageResource(R.drawable.s1_rating_star_sel);
                ib_rating5.setImageResource(R.drawable.s1_rating_star_def);
            }
        });

        ib_rating5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = 5;
                ib_rating1.setImageResource(R.drawable.s1_rating_star_sel);
                ib_rating2.setImageResource(R.drawable.s1_rating_star_sel);
                ib_rating3.setImageResource(R.drawable.s1_rating_star_sel);
                ib_rating4.setImageResource(R.drawable.s1_rating_star_sel);
                ib_rating5.setImageResource(R.drawable.s1_rating_star_sel);
            }
        });

        rv_snap_photos = (RecyclerView) view.findViewById(R.id.rv_snap_photos);
        rv_snap_photos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        rv_snap_photos.setAdapter(createNewAdapter(bitmaps));

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
                    selIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
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

        final RelativeLayout ll_btn_cam = (RelativeLayout) view.findViewById(R.id.ll_btn_cam);
        ll_btn_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideUp(rl_choose_photo);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);

        Log.d(LOGGER_TAG, "requestCode: " + requestCode + "==resultCode: " + resultCode);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && data != null) {
            Log.d(LOGGER_TAG, "captured!!! "+mCurrentPhotoPath);

            Bundle extras = data.getExtras();
//            Map<String, Object> bitmapResult = mUtils.processBitmap(getActivity(), (Bitmap) extras.get("data"), 500);
            Map<String, Object> bitmapResult = mUtils.processBitmap(getActivity(), (Bitmap) extras.get("data"), glMaxTextureSize);
            String error = mUtils.validateUploadImage(bitmapResult);

            if(error.isEmpty()) {
                bitmaps.add(bitmapResult.get(Constants.PHOTO_PATH).toString());

                SharedPref sharedPref = new SharedPref(getContext());
                sharedPref.setSelectedPostPhotos(bitmaps);

                rv_snap_photos.setAdapter(createNewAdapter(bitmaps));
            } else
                mUtils.getErrorDialog(error).show();

        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) { // && data.getData() != null
            Log.d(LOGGER_TAG, "selected!!!");

            //If Single image selected then it will fetch from Gallery
            if(data.getData()!=null){

                try {
                    Map<String, Object> bitmapResult = mUtils.processBitmap(getActivity(), MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData()), glMaxTextureSize);
                    Log.d("LOG_TAG", "SINGLE Selected Image: " + bitmapResult.get(Constants.PHOTO_PATH));

                    String error = mUtils.validateUploadImage(bitmapResult);
                    if(error.isEmpty()) {
                        bitmaps.add(bitmapResult.get(Constants.PHOTO_PATH).toString());

                        SharedPref sharedPref = new SharedPref(getContext());
                        sharedPref.setSelectedPostPhotos(bitmaps);
                    } else
                        mUtils.getErrorDialog(error).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                rv_snap_photos.setAdapter(createNewAdapter(bitmaps));

            }else{
                if(data.getClipData()!=null){

                    Uri uri;

                    ClipData mClipData=data.getClipData();

                    if(mClipData.getItemCount() > (5-bitmaps.size())) {
                        mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_maximum_no_images)).show();
                    } else {
                        String error = "";
                        for(int i=0;i<mClipData.getItemCount();i++){

                            ClipData.Item item = mClipData.getItemAt(i);
                            uri = item.getUri();

                            try {
//
                                Map<String, Object> bitmapResult = mUtils.processBitmap(getActivity(), MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri), glMaxTextureSize);
                                Log.d("LOG_TAG", "MULTI Selected Images" + bitmapResult.get(Constants.PHOTO_PATH));

                                error = mUtils.validateUploadImage(bitmapResult);
                                if(error.isEmpty()) {
                                    bitmaps.add(bitmapResult.get(Constants.PHOTO_PATH).toString());
                                    Log.d("LOG_TAG", "Selected Images Total: " + bitmaps.size());

                                    SharedPref sharedPref = new SharedPref(getContext());
                                    sharedPref.setSelectedPostPhotos(bitmaps);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        Log.d("LOG_TAG", "Selected Images Total: " + bitmaps.size());
                        rv_snap_photos.setAdapter(createNewAdapter(bitmaps));

                        if(error.isEmpty() == false)
                            mUtils.getErrorDialog(error).show();

                    }
                }

            }

        }
    }

    public PostNewSnapRecyclerViewAdapter createNewAdapter(List<String> b) {
        return new PostNewSnapRecyclerViewAdapter(getContext(), b, R.layout.list_rv_snap_photo_item,
                new PostNewSnapRecyclerViewAdapter.PostNewSnapRecyclerViewAdapterListener() {
                    @Override
                    public void removeBitmap(String bitmap) {
                        bitmaps.remove(bitmap);
//                        adapter.swap(bitmaps);
                        rv_snap_photos.setAdapter(createNewAdapter(bitmaps));
                    }
                });
    }

    @Override
    public void onAttach(Context context) {
        Log.d(LOGGER_TAG, "attached");
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
        Log.d(LOGGER_TAG, "detached");
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOGGER_TAG, "resume postSnapFragment");
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
    public void onPause() {
        Log.d(LOGGER_TAG, "pause");
        super.onPause();
        VolleySingleton.getInstance(getContext()).getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });

        SharedPref sharedPref = new SharedPref(getContext());
        sharedPref.setSelectedPostPhotos(bitmaps);
    }

    public void customResumeFromBack(int type, Object object){
        //if(object instanceof Restaurant) {
        if(type == HotSearch.Category.DISTRICT.getKey()) {
            restaurant = (Restaurant) object;
            if(restaurant != null && restaurant.getName() != null) {
                if(restaurant.getName().isEmpty() == false) {
                    tv_snap_resto.setText(restaurant.getName());
                    tv_snap_location.setText(restaurant.getLocation());
                }
            }
        } else if (type == HotSearch.Category.SPENDINGS.getKey()) {//else if (object instanceof Spending) {
            spendingList = (List<String>) object;
            // only one spending
            if(spendingList.isEmpty() == false) {
                try {
                    HotSearchFilterDataSource dataSource = new HotSearchFilterDataSource(getContext());
                    dataSource.open();

                    HotSearch temp = dataSource.getRowByIdAndCategory(Integer.parseInt(spendingList.get(0)), HotSearch.Category.SPENDINGS.getKey());

                    dataSource.close();
                    Log.d(LOGGER_TAG, "temp name: " + temp.getValue());
                    tv_snap_spending.setText(temp.getValue());

                    spending = new Spending();
                    spending.setId(temp.getId());

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        } else {
            dish = (List<String>) object;
            if(dish.isEmpty() == false) {
                try {
                    HotSearchFilterDataSource dataSource = new HotSearchFilterDataSource(getContext());
                    dataSource.open();

                    int id = Integer.parseInt(dish.get(0));
                    if(id == -1)
                        id = Integer.parseInt(dish.get(1));

                    HotSearch temp = dataSource.getRowByIdAndCategory(id, HotSearch.Category.DISH.getKey());

                    dataSource.close();
                    Log.d(LOGGER_TAG, "temp name: " + temp.getValue());
                    tv_snap_dish.setText(temp.getValue());

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
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
        void goBack();
        void showHotSearchMore(List<String> defaultSelected, int type, boolean isSingleSelect, boolean isSelectedValueValue);
        void showHotSearchMoreShowAll(List<String> defaultSelected, int type, boolean isSingleSelect, boolean isSelectedValueValue, boolean showAllOption);
        void showRestaurantList(Restaurant restaurant);
        void showStartingFragmentFromLogout();
        void showUserProfile(int userId);
    }
}
