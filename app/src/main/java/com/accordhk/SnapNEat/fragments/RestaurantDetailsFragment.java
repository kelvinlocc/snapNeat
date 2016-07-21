package com.accordhk.SnapNEat.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.adapters.RestaurantListImageRecyclerViewAdapter;
import com.accordhk.SnapNEat.adapters.SnapListSnapRecyclerViewAdapter;
import com.accordhk.SnapNEat.models.Dish;
import com.accordhk.SnapNEat.models.ResponseRestaurantDetails;
import com.accordhk.SnapNEat.models.Restaurant;
import com.accordhk.SnapNEat.services.ApiWebServices;
import com.accordhk.SnapNEat.utils.Constants;
import com.accordhk.SnapNEat.utils.CustomFontButton;
import com.accordhk.SnapNEat.utils.CustomFontTextView;
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
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RestaurantDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RestaurantDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RestaurantDetailsFragment extends BaseFragment {
    private static String LOGGER_TAG = "RestaurantDetailsFragment";

    public static final String RESTAURANT_ID = "restaurantId";

    private int restaurantId;

    private OnFragmentInteractionListener mListener;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    // wechat
    private IWXAPI iwapi;

    public RestaurantDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment RestaurantDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RestaurantDetailsFragment newInstance(int param1) {
        RestaurantDetailsFragment fragment = new RestaurantDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(RESTAURANT_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            restaurantId = getArguments().getInt(RESTAURANT_ID);
        }

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

//        // start: wechat
        iwapi = WXAPIFactory.createWXAPI(getContext(), Constants.WECHAT_APPID, false);
//        // end: wechat
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int width = size.x;
        final int height = size.y/2;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant_details, container, false);

        final RelativeLayout rl_share = (RelativeLayout) view.findViewById(R.id.rl_share);

        RelativeLayout rl_share_fb = (RelativeLayout) view.findViewById(R.id.rl_share_fb);
        RelativeLayout rl_share_instagram = (RelativeLayout) view.findViewById(R.id.rl_share_instagram);
        RelativeLayout rl_share_wechat = (RelativeLayout) view.findViewById(R.id.rl_share_wechat);

        ImageButton btn_back = (ImageButton) view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.goBack();
                }
            }
        });

        final CustomFontTextView btn_share = (CustomFontTextView) view.findViewById(R.id.btn_share);
        btn_share.setText(mUtils.getStringResource(R.string.s10_share));

        final CustomFontTextView title = (CustomFontTextView) view.findViewById(R.id.title);

        final RecyclerView iv_restaurant_image = (RecyclerView) view.findViewById(R.id.iv_restaurant_image);
        final ImageView rating = (ImageView) view.findViewById(R.id.rating);

        final CustomFontTextView tv_address = (CustomFontTextView) view.findViewById(R.id.tv_address);
        final ImageView iv_navigate = (ImageView) view.findViewById(R.id.iv_navigate);

        final CustomFontTextView tv_phone = (CustomFontTextView) view.findViewById(R.id.tv_phone);

        final CustomFontTextView tv_dish = (CustomFontTextView) view.findViewById(R.id.tv_dish);
        final CustomFontTextView tv_spending = (CustomFontTextView) view.findViewById(R.id.tv_spending);
        final CustomFontTextView tv_hours = (CustomFontTextView) view.findViewById(R.id.tv_hours);
        final CustomFontTextView tv_website = (CustomFontTextView) view.findViewById(R.id.tv_website);

        final RecyclerView rv_food_photos = (RecyclerView) view.findViewById(R.id.rv_food_photos);

        final ImageButton btn_facebook = (ImageButton) view.findViewById(R.id.btn_facebook);
        if(isPackageInstalled(Constants.FACEBOOK_PACKAGE))
            rl_share_fb.setVisibility(View.VISIBLE);
        else
            rl_share_fb.setVisibility(View.GONE);

        final ImageButton btn_wechat = (ImageButton) view.findViewById(R.id.btn_wechat);
        if (isPackageInstalled(Constants.WECHAT_PACKAGE))
            rl_share_wechat.setVisibility(View.VISIBLE);
        else
            rl_share_wechat.setVisibility(View.GONE);

        final ImageButton btn_instagram = (ImageButton) view.findViewById(R.id.btn_instagram);
        if(isPackageInstalled(Constants.INSTAGRAM_PACKAGE))
            rl_share_instagram.setVisibility(View.VISIBLE);
        else
            rl_share_instagram.setVisibility(View.GONE);

        final ImageButton btn_others = (ImageButton) view.findViewById(R.id.btn_others);
        final CustomFontButton btn_cancel = (CustomFontButton) view.findViewById(R.id.btn_cancel);

        try {
            Map<String, String> params = mUtils.getBaseRequestMap();
            params.put(Restaurant.RESTAURANT_NID, String.valueOf(restaurantId));

            mProgressDialog.show();
            mApi.getRestaurantDetails(params, new ApiWebServices.ApiListener() {
                @Override
                public void onResponse(Object object) {
                    try {
                        final ResponseRestaurantDetails response = (ResponseRestaurantDetails) object;

                        if (response != null) {
                            if (response.getStatus() != Constants.RES_SUCCESS) {
                                mUtils.dismissDialog(mProgressDialog);
                                mUtils.getErrorDialog(response.getMessage()).show();
                            } else {

                                final Restaurant restaurant = response.getRestaurantInfo();

                                btn_share.setText(mUtils.getStringResource(R.string.s10_share));
                                btn_share.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        slideUp(rl_share);
                                    }
                                });

                                btn_facebook.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mPopupBackStack.pop();
                                        slideDown(rl_share);
                                        if (ShareDialog.canShow(ShareLinkContent.class)) {
                                            try {
                                                shareDialog.show(generateFBShareContent(restaurant, false));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });

                                btn_wechat.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mPopupBackStack.pop();
                                        slideDown(rl_share);
                                        iwapi.sendReq(generateWeChatShareContent(restaurant, false));
                                    }
                                });

                                btn_instagram.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mPopupBackStack.pop();
                                        slideDown(rl_share);
                                        // Create the new Intent using the 'Send' action.
                                        final Intent share = new Intent(Intent.ACTION_SEND);
                                        share.setPackage(Constants.INSTAGRAM_PACKAGE);

                                        // Set the MIME type
                                        share.setType("image/*");

                                        if (restaurant.getPhotos().isEmpty() == false) {
                                            try {

                                                if(restaurant.getPhotos().size() > 0) {
                                                    if(restaurant.getPhotos().get(0).getImage() != null && restaurant.getPhotos().get(0).getImage().trim().isEmpty() == false) {
                                                        showPermitSaveExternal(share, restaurant.getPhotos().get(0).getImage().trim());
                                                    } else {
                                                        mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_no_image_to_share)).show();
                                                    }
                                                } else {
                                                    mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_no_image_to_share)).show();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_no_image_to_share)).show();
                                            }
                                        } else {
                                            mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_no_image_to_share)).show();
                                        }

                                    }
                                });

                                btn_others.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mPopupBackStack.pop();
                                        slideDown(rl_share);

                                        // Create the new Intent using the 'Send' action.
                                        final Intent share = new Intent(Intent.ACTION_SEND);

                                        // Set the MIME type
                                        share.setType("image/*");

                                        if (restaurant.getPhotos().isEmpty() == false) {
                                            try {

                                                if(restaurant.getPhotos().size() > 0) {
                                                    if(restaurant.getPhotos().get(0).getImage() != null && restaurant.getPhotos().get(0).getImage().trim().isEmpty() == false) {
                                                        showPermitSaveExternal(share, restaurant.getPhotos().get(0).getImage().trim());
                                                    } else {
                                                        mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_no_image_to_share)).show();
                                                    }
                                                } else {
                                                    mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_no_image_to_share)).show();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_no_image_to_share)).show();
                                            }
                                        } else {
                                            mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_no_image_to_share)).show();
                                        }
                                    }
                                });

                                btn_cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mPopupBackStack.pop();
                                        slideDown(rl_share);
                                    }
                                });

                                title.setText(restaurant.getName());

                                RestaurantListImageRecyclerViewAdapter restoAdapter = new RestaurantListImageRecyclerViewAdapter(getContext(), restaurant.getPhotos(), R.layout.list_snap_details_snap_photo,
                                        width, height,
                                        new RestaurantListImageRecyclerViewAdapter.RestaurantListImageRecyclerViewAdapterListener() {
                                            @Override
                                            public void showSnapDetails(int snapId) {
                                                if (mListener != null)
                                                    mListener.showSnapDetails(snapId);
                                            }
                                        });

                                iv_restaurant_image.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                                iv_restaurant_image.setAdapter(restoAdapter);

                                int id = getContext().getResources().getIdentifier("s1_rating_" + restaurant.getRating(), "drawable", getContext().getPackageName());
                                rating.setImageResource(id);

                                tv_address.setText(restaurant.getLocation());

                                iv_navigate.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (mListener != null)
                                            mListener.showGMapFragment(restaurantId, restaurant.getLatitude(), restaurant.getLongitude(), restaurant.getName(), restaurant.getName());
                                    }
                                });

                                tv_phone.setText(restaurant.getContact());

                                String dish = "";

                                for (Dish d : restaurant.getDishes()) {
                                    if (!dish.isEmpty())
                                        dish = dish + " / ";
                                    dish = dish + d.getTitle();
                                }
                                tv_dish.setText(dish);

                                tv_spending.setText(restaurant.getSpending().getTitle());

                                tv_hours.setText(restaurant.getHours());

                                tv_website.setText(restaurant.getWebsite());

                                SnapListSnapRecyclerViewAdapter adapter = new SnapListSnapRecyclerViewAdapter(getContext(), restaurant.getRestoFood(), R.layout.list_gv_snap_item,
                                        new SnapListSnapRecyclerViewAdapter.SnapListSnapRecyclerViewAdapterListener() {
                                            @Override
                                            public void showSnapDetails(int snapId) {
                                                if (mListener != null) {
                                                    mListener.showRestaurantDetails(snapId);
                                                }
                                            }
                                        });

                                rv_food_photos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                                rv_food_photos.setAdapter(adapter);

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
                    error.printStackTrace();
                    mUtils.dismissDialog(mProgressDialog);
                    mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_cannot_process_request)).show();
                }
            });
        } catch (Exception e){
            mUtils.dismissDialog(mProgressDialog);
            e.printStackTrace();
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
        iwapi.unregisterApp();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case 0x101: {

            } default:
                callbackManager.onActivityResult(requestCode, resultCode, data);
                Log.d(LOGGER_TAG, "result " + ShareDialog.canShow(ShareLinkContent.class));
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
        void showGMapFragment(int id, float latitude, float longitude, String title, String name);
        void showRestaurantDetails(int restaurantId);
        void showSnapDetails(int snapId);
    }
}
