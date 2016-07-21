package com.accordhk.SnapNEat.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.models.Restaurant;
import com.accordhk.SnapNEat.models.Snap;
import com.accordhk.SnapNEat.models.User;
import com.accordhk.SnapNEat.services.ApiWebServices;
import com.accordhk.SnapNEat.utils.Constants;
import com.accordhk.SnapNEat.utils.SharedPref;
import com.accordhk.SnapNEat.utils.Utils;
import com.accordhk.SnapNEat.utils.VolleySingleton;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.facebook.share.model.ShareLinkContent;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;

import java.io.File;
import java.util.Map;
import java.util.Stack;

/**
 * Created by jm on 5/3/16.
 */
public class BaseFragment extends Fragment {

    public Context mContext;
    public Utils mUtils;
    public ApiWebServices mApi;
    public ProgressDialog mProgressDialog;

    public Stack<RelativeLayout> mPopupBackStack;

    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 123;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mUtils = new Utils(mContext);
        mApi = new ApiWebServices().getInstance(mContext);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(mUtils.getStringResource(R.string.common_loading));
        mPopupBackStack = new Stack<>();
    }

    // START SLIDE
    public void slideUp(RelativeLayout slide_layout) {
        hideKeyboard();
        mPopupBackStack.add(slide_layout);
        slide_layout.setVisibility(View.VISIBLE);
//        TranslateAnimation slide = new TranslateAnimation(0, 0, screenHeight(), 0);
//        slide.setDuration(300);
//        slide.setFillAfter(true);
//        slide_layout.startAnimation(slide);
    }

    public void slideDown(RelativeLayout slide_layout) {
//        slide_layout.setVisibility(View.VISIBLE);
//        TranslateAnimation slide = new TranslateAnimation(0, 0, 0, 2*screenHeight());
//        slide.setDuration(300);
//        slide.setFillAfter(true);
//        slide_layout.startAnimation(slide);
        slide_layout.setVisibility(View.INVISIBLE);

    }

    private int screenHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;
    }
    // END SLIDE

    public boolean isPackageInstalled(String packagename) {
        PackageManager pm = mContext.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * Generates the FB Share Link Content
     * @param obj Snap or Restaurant detail
     * @param isSnap true if obj is Snap; false if Restaurant
     * @return
     */
    public ShareLinkContent generateFBShareContent (Object obj, boolean isSnap) {

        String username = "";
        String title = "";

        User user = new SharedPref(getContext()).getLoggedInUser();
        if(user != null) {
            username = user.getUsername();
        }

        if(isSnap) { // snap

            Snap s = (Snap) obj;

            String thumbnail = "";
            try {
                thumbnail = s.getImageThumbnail().trim();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(thumbnail.isEmpty()) {
                if(s.getPhotos().size() > 0) {
                    try {
                        thumbnail = s.getPhotos().get(0).toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            Log.d("Share url: ", Constants.DEEPLINK_SNAP_URL + s.getnId().toString());

            title = s.getTitle()+" "+Uri.parse(Constants.DEEPLINK_SNAP_URL + s.getnId().toString());
            if (username != null && username.isEmpty() == false)
                title = String.format(mUtils.getStringResource(R.string.fb_share_title), username)+" "+title;

            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(Constants.FACEBOOK_URL))
                    .setImageUrl(Uri.parse(thumbnail))
                    .setContentTitle(s.getTitle())
                    .setQuote(title)
                    .build();
            return content;
        }

        //  else restaurant
        Restaurant restaurant = (Restaurant) obj;

        Log.d("Share url: ", Constants.DEEPLINK_RESTAURANT_URL + restaurant.getnId().toString());

        title = restaurant.getName()+" "+Uri.parse(Constants.DEEPLINK_RESTAURANT_URL + restaurant.getnId().toString());
        if (username != null && username.isEmpty() == false)
            title = String.format(mUtils.getStringResource(R.string.fb_share_title), username)+" "+title;

//        ShareLinkContent content = new ShareLinkContent.Builder()
//                .setContentUrl(Uri.parse(Constants.DEEPLINK_RESTAURANT_URL + restaurant.getnId().toString()))
//                .setContentTitle(restaurant.getName())
//                .setContentDescription(restaurant.getDescription())
//                .build();
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl((Uri.parse(Constants.FACEBOOK_URL)))
                .setImageUrl(Uri.parse(restaurant.getPhotos().get(0).getImageThumbnail()))
                .setContentTitle(restaurant.getName())
                .setQuote(title)
                .build();
        return content;
    }

    /**
     * Generates the WeChat Share Content
     * @param obj Snap or Restaurant detail
     * @param isSnap true if obj is Snap; false if Restaurant
     * @return
     */
    public SendMessageToWX.Req generateWeChatShareContent (Object obj, boolean isSnap) {
            int THUMB_SIZE = 150;

//        WXTextObject wxTextObject = new WXTextObject();
//        WXMediaMessage mediaMessage = new WXMediaMessage();
//        SendMessageToWX.Req req = new SendMessageToWX.Req();
//
//        if(isSnap) { // snap
//            Snap s = (Snap) obj;
//            wxTextObject.text = s.getTitle();
//
//            mediaMessage.mediaObject = wxTextObject;
//            mediaMessage.description = Constants.DEEPLINK_SNAP_URL+s.getnId();
//
//        } else { // restaurant
//            Restaurant restaurant = (Restaurant) obj;
//            wxTextObject.text = restaurant.getName();
//
//            mediaMessage.mediaObject = wxTextObject;
//            mediaMessage.description = Constants.DEEPLINK_RESTAURANT_URL + restaurant.getnId();
//        }
//
//        req.transaction = String.valueOf(System.currentTimeMillis());
//        req.message = mediaMessage;
//        req.scene = SendMessageToWX.Req.WXSceneSession;

        WXWebpageObject webpage = new WXWebpageObject();
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        WXMediaMessage msg;
        if(isSnap) { // snap
            Snap s = (Snap) obj;
            webpage.webpageUrl = Constants.DEEPLINK_SNAP_URL+s.getnId();
            msg = new WXMediaMessage(webpage);

            msg.title = s.getTitle();
            try {

                msg.description = s.getDistrict().getName();
//                if(s.getCommens() != null && s.getCommens().trim().isEmpty() == false)
//                    msg.description = s.getCommens();
//                else if(s.getComments() != null && s.getComments().trim().isEmpty() == false)
//                    msg.description = s.getComments();

            } catch (Exception e) {
                e.printStackTrace();
            }

//                Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//                msg.thumbData = Util.bmpToByteArray(thumb, true);
            try {
                if(s.getPhotos() != null && s.getPhotos().isEmpty() == false) {
                    Bitmap bmp = mUtils.getBitmapFromURL(s.getPhotos().get(0));
                    //thumbnail of image
                    Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
                    bmp.recycle();
                    msg.thumbData = Util.bmpToByteArray(thumbBmp, true); //set the thumbnail

                } else {
                    Bitmap bmp = mUtils.getBitmapFromURL(s.getImageThumbnail());
                    //thumbnail of image
                    Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
                    bmp.recycle();
                    msg.thumbData = Util.bmpToByteArray(thumbBmp, true); //set the thumbnail

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            req.message = msg;

        } else { // restaurant
            Restaurant restaurant = (Restaurant) obj;
            webpage.webpageUrl = Constants.DEEPLINK_RESTAURANT_URL + restaurant.getnId();
            msg = new WXMediaMessage(webpage);

            msg.title = restaurant.getName();
            msg.description = restaurant.getDescription();

            try {
                Bitmap bmp = mUtils.getBitmapFromURL(restaurant.getPhotos().get(0).getImageThumbnail());
                //thumbnail of image
                Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
                bmp.recycle();
                msg.thumbData = Util.bmpToByteArray(thumbBmp, true); //set the thumbnail
            } catch (Exception e) {
                e.printStackTrace();
            }

            req.message = msg;
        }

        req.transaction = String.valueOf(System.currentTimeMillis());
        req.scene = SendMessageToWX.Req.WXSceneSession;

        return req;
    }

    public void showPermitSaveExternal(Intent si, String strUrl){
        final Intent shareIntent = si;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            ImageRequest ir = new ImageRequest(strUrl, new Response.Listener<Bitmap>() {

                @Override
                public void onResponse(Bitmap response) {
                    try {
                        Map<String, Object> bitmapResult = mUtils.processBitmap(getActivity(), response);

                        if(bitmapResult.get(Constants.PHOTO_PATH).toString().trim().isEmpty() == false) {
                            // Create the URI from the media
                            File media = new File(bitmapResult.get(Constants.PHOTO_PATH).toString());
                            Uri shareUri = Uri.fromFile(media);
                            // Add the URI to the Intent.
                            shareIntent.putExtra(Intent.EXTRA_STREAM, shareUri);

                            // Broadcast the Intent.
                            startActivity(Intent.createChooser(shareIntent, "Share to"));
                        } else
                            mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_no_image_to_share)).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_no_image_to_share)).show();
                    }
                }
            }, 0, 0, null, null);

            ir.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            VolleySingleton.getInstance(getContext()).addToRequestQueue(ir);
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            mUtils.dismissDialog(mProgressDialog);
        } catch (Exception e) {
            e.printStackTrace();
        }
        hideKeyboard();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        hideKeyboard();
    }

    public void hideKeyboard(){
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    public Stack<RelativeLayout> getmPopupBackStack() {
        return mPopupBackStack;
    }

    public int getGLMaxTextureSize() {
        try {
            EGLDisplay dpy = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
            int[] vers = new int[2];
            EGL14.eglInitialize(dpy, vers, 0, vers, 1);

            int[] configAttr = {
                    EGL14.EGL_COLOR_BUFFER_TYPE, EGL14.EGL_RGB_BUFFER,
                    EGL14.EGL_LEVEL, 0,
                    EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                    EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
                    EGL14.EGL_NONE
            };
            EGLConfig[] configs = new EGLConfig[1];
            int[] numConfig = new int[1];
            EGL14.eglChooseConfig(dpy, configAttr, 0,
                    configs, 0, 1, numConfig, 0);
            if (numConfig[0] == 0) {
                // TROUBLE! No config found.
            }
            EGLConfig config = configs[0];

            int[] surfAttr = {
                    EGL14.EGL_WIDTH, 64,
                    EGL14.EGL_HEIGHT, 64,
                    EGL14.EGL_NONE
            };
            EGLSurface surf = EGL14.eglCreatePbufferSurface(dpy, config, surfAttr, 0);

            int[] ctxAttrib = {
                    EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                    EGL14.EGL_NONE
            };
            EGLContext ctx = EGL14.eglCreateContext(dpy, config, EGL14.EGL_NO_CONTEXT, ctxAttrib, 0);

            EGL14.eglMakeCurrent(dpy, surf, surf, ctx);

            int[] maxSize = new int[1];
            GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxSize, 0);

            EGL14.eglMakeCurrent(dpy, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                    EGL14.EGL_NO_CONTEXT);
            EGL14.eglDestroySurface(dpy, surf);
            EGL14.eglDestroyContext(dpy, ctx);
            EGL14.eglTerminate(dpy);

            int max = Math.min(maxSize[0], Constants.IMAGE_MAX_BITMAP_DIMENSION);
            Log.d("BaseFragment", "MAX TEXTURE SIZE: "+max);

            return max;
        } catch (Exception e) {
            Log.e("BaseFragment", "Exception occured while trying to get max texture size: "+e.getLocalizedMessage());
        }

        return Constants.IMAGE_MAX_BITMAP_DIMENSION;
    }
}
