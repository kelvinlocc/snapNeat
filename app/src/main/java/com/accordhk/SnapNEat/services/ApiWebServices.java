package com.accordhk.SnapNEat.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import com.accordhk.SnapNEat.dao.HotSearchFilterDataSource;
import com.accordhk.SnapNEat.models.BaseResponse;
import com.accordhk.SnapNEat.models.HotSearch;
import com.accordhk.SnapNEat.models.ResponseBaseWithId;
import com.accordhk.SnapNEat.models.ResponseFileUploadSettings;
import com.accordhk.SnapNEat.models.ResponseFollowUser;
import com.accordhk.SnapNEat.models.ResponseFootprints;
import com.accordhk.SnapNEat.models.ResponseHotSearchFilter;
import com.accordhk.SnapNEat.models.ResponseListSnaps;
import com.accordhk.SnapNEat.models.ResponseListUsers;
import com.accordhk.SnapNEat.models.ResponseLogin;
import com.accordhk.SnapNEat.models.ResponsePostLike;
import com.accordhk.SnapNEat.models.ResponseReasonInappropriate;
import com.accordhk.SnapNEat.models.ResponseRestaurantDetails;
import com.accordhk.SnapNEat.models.ResponseRestaurants;
import com.accordhk.SnapNEat.models.ResponseSnapDetails;
import com.accordhk.SnapNEat.models.ResponseSnapsHomepage;
import com.accordhk.SnapNEat.models.ResponseUserProfile;
import com.accordhk.SnapNEat.models.Snap;
import com.accordhk.SnapNEat.models.User;
import com.accordhk.SnapNEat.utils.Constants;
import com.accordhk.SnapNEat.utils.GsonRequest;
import com.accordhk.SnapNEat.utils.HttpsTrustManager;
import com.accordhk.SnapNEat.utils.MultipartRequest;
import com.accordhk.SnapNEat.utils.Utils;
import com.accordhk.SnapNEat.utils.VolleySingleton;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Created by jm on 22/1/16.
 */
public class ApiWebServices implements IApi {

    private static String LOGGER_TAG = "ApiWebServices";

    private static boolean isTest = false; // if true, then get from DummyData; else retrieve from server
    private static ApiWebServices apiWebService;
    private static ApiWebServices.ApiListener mListener;

    public static Context mContext;

    private static Gson GSON = new Gson();

    private final int MY_SOCKET_TIMEOUT_MS = 30000;

    private Utils mUtils;

    public interface ApiListener {
        public void onResponse(Object object);
        public void onErrorResponse(VolleyError error);
    }

    public ApiWebServices() {}

    //TODO: set here the selected_language param
    public ApiWebServices(Context c) {
        mContext = c;
        mUtils = new Utils(c);
    }

    public ApiWebServices getInstance(Context c) {

        if(isTest == true) {
            Log.d(LOGGER_TAG, "Getting from DummyData");
            apiWebService = new DummyData();
        } else {
            if(apiWebService == null) {
                Log.d(LOGGER_TAG, "Getting from ApiWebServices");
                HttpsTrustManager.allowAllSSL();
                apiWebService = new ApiWebServices(c);

            }
        }
        return apiWebService;
    }

    @Override
    public void registerUser(Map<String, String> params, ApiListener listener) throws Exception {
        mListener = listener;

        GsonRequest<BaseResponse> request = new GsonRequest<BaseResponse>(Method.POST, Constants.USER_REGISTER, BaseResponse.class, params, null,
                new Response.Listener<BaseResponse>() {
                    @Override
                    public void onResponse(BaseResponse response) {
                        String jsonOutput = GSON.toJson(response);
                        Log.d(LOGGER_TAG, "registerUser: "+jsonOutput);
                        mListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    @Override
    public void loginUser(Map<String, String> params, ApiListener listener) throws Exception {
        mListener = listener;
        GsonRequest<ResponseLogin> request = new GsonRequest<ResponseLogin>(Method.POST, Constants.USER_LOGIN, ResponseLogin.class, params, null,
                new Response.Listener<ResponseLogin>() {
                    @Override
                    public void onResponse(ResponseLogin response) {
                        String jsonOutput = GSON.toJson(response);
                        Log.d(LOGGER_TAG, "loginUser: "+jsonOutput);
                        mListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    @Override
    public void fbloginUser(Map<String, String> params, ApiListener listener) throws Exception {
        mListener = listener;
        GsonRequest<ResponseLogin> request = new GsonRequest<ResponseLogin>(Method.POST, Constants.USER_FBLOGIN, ResponseLogin.class, params, null,
                new Response.Listener<ResponseLogin>() {
                    @Override
                    public void onResponse(ResponseLogin response) {
                        String jsonOutput = GSON.toJson(response);
                        Log.d(LOGGER_TAG, "fbloginUser: "+jsonOutput);
                        mListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);

    }

    @Override
    public void logoutUser(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        mListener = listener;
        GsonRequest<BaseResponse> request = new GsonRequest<BaseResponse>(Method.POST, Constants.USER_LOGOUT, BaseResponse.class, params, header,
                new Response.Listener<BaseResponse>() {
                    @Override
                    public void onResponse(BaseResponse response) {
                        String jsonOutput = GSON.toJson(response);
                        Log.d(LOGGER_TAG, "logoutUser: "+jsonOutput);
                        mListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    @Override
    public void getUserProfile(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        mListener = listener;
        String url = Constants.USER_PROFILE+urlEncodeUTF8(params);

        Log.d(LOGGER_TAG, "URL: "+url);
        GsonRequest<ResponseUserProfile> request = new GsonRequest<ResponseUserProfile>(Method.GET, url, ResponseUserProfile.class, null, header,
                new Response.Listener<ResponseUserProfile>() {
                    @Override
                    public void onResponse(ResponseUserProfile response) {
                        String jsonOutput = GSON.toJson(response);
                        Log.d(LOGGER_TAG, "getUserProfile: "+jsonOutput);
                        mListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

//    @Override
//    public void updateUserProfile(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
//        mListener = listener;
//        GsonRequest<BaseResponse> request = new GsonRequest<BaseResponse>(Method.POST, Constants.USER_PROFILE_UPDATE, BaseResponse.class, params, header,
//                new Response.Listener<BaseResponse>() {
//                    @Override
//                    public void onResponse(BaseResponse response) {
//                        String jsonOutput = GSON.toJson(response);
//                        Log.d(LOGGER_TAG, "updateUserProfile: "+jsonOutput);
//                        mListener.onResponse(response);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                        mListener.onErrorResponse(error);
//                    }
//                });
//
//        request.setRetryPolicy(new DefaultRetryPolicy(
//                MY_SOCKET_TIMEOUT_MS,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
//    }

    @Override
    public void updateUserProfile(Map<String, String> params, Map<String, byte[]> imageMultiPart, Map<String, String> header, ApiListener listener) throws Exception {
//        mListener = listener;
//        GsonRequest<BaseResponse> request = new GsonRequest<BaseResponse>(Method.POST, Constants.USER_PROFILE_UPDATE, BaseResponse.class, params, header,
//                new Response.Listener<BaseResponse>() {
//                    @Override
//                    public void onResponse(BaseResponse response) {
//                        String jsonOutput = GSON.toJson(response);
//                        Log.d(LOGGER_TAG, "updateUserProfile: "+jsonOutput);
//                        mListener.onResponse(response);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                        mListener.onErrorResponse(error);
//                    }
//                });
//
//        request.setRetryPolicy(new DefaultRetryPolicy(
//                MY_SOCKET_TIMEOUT_MS,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//        VolleySingleton.getInstance(mContext).addToRequestQueue(request);

        mListener = listener;
        final Map<String, String> headers = header;

        String boundary = "snemobile-" + System.currentTimeMillis();
        String mimeType = "multipart/form-data;boundary=" + boundary;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        byte[] multipartBody = null;

        try {

//            for (Map.Entry<String, String> p: params.entrySet()) {
//                mUtils.buildJsonPart(boundary, dos, p.getKey(), p.getValue());
//            }

            // files
            for (Map.Entry<String, byte[]> file: imageMultiPart.entrySet()) {
                mUtils.buildFilePart(boundary, dos, User.AVATAR, file.getValue(), file.getKey());
            }

            // send multipart form data necesssary after file data
            dos.writeBytes(mUtils.twoHyphens + boundary + mUtils.twoHyphens + mUtils.lineEnd);
            // pass to multipart body
            multipartBody = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String url = Constants.USER_PROFILE_UPDATE+urlEncodeUTF8(params);

        MultipartRequest request = new MultipartRequest(Method.POST, url, headers, mimeType, multipartBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOGGER_TAG, response.toString());
                        mListener.onResponse(GSON.fromJson(response.toString(), ResponseBaseWithId.class));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    @Override
    public void changePassword(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        mListener = listener;
        GsonRequest<BaseResponse> request = new GsonRequest<BaseResponse>(Method.POST, Constants.USER_CHANGE_PASSWORD, BaseResponse.class, params, header,
                new Response.Listener<BaseResponse>() {
                    @Override
                    public void onResponse(BaseResponse response) {
                        String jsonOutput = GSON.toJson(response);
                        Log.d(LOGGER_TAG, "changePassword: "+jsonOutput);
                        mListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    @Override
    public void resetPassword(Map<String, String> params, ApiListener listener) throws Exception {
        mListener = listener;
        GsonRequest<BaseResponse> request = new GsonRequest<BaseResponse>(Method.POST, Constants.USER_RESET_PASSWORD, BaseResponse.class, params, null,
                new Response.Listener<BaseResponse>() {
                    @Override
                    public void onResponse(BaseResponse response) {
                        String jsonOutput = GSON.toJson(response);
                        Log.d(LOGGER_TAG, "resetPassword: "+jsonOutput);
                        mListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    @Override
    public void getMyFollowings(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        mListener = listener;
        String url = Constants.USER_FOLLOWINGS+urlEncodeUTF8(params);

        Log.d(LOGGER_TAG, "URL: "+url);
        GsonRequest<ResponseListUsers> request = new GsonRequest<ResponseListUsers>(Method.GET, url, ResponseListUsers.class, null, header,
                new Response.Listener<ResponseListUsers>() {
                    @Override
                    public void onResponse(ResponseListUsers response) {
                        String jsonOutput = GSON.toJson(response);
                        Log.d(LOGGER_TAG, "getMyFollowings: "+jsonOutput);
                        mListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    @Override
    public void getMyFollowers(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        mListener = listener;
        String url = Constants.USER_FOLLOWERS+urlEncodeUTF8(params);

        Log.d(LOGGER_TAG, "URL: "+url);
        GsonRequest<ResponseListUsers> request = new GsonRequest<ResponseListUsers>(Method.GET, url, ResponseListUsers.class, null, header,
                new Response.Listener<ResponseListUsers>() {
                    @Override
                    public void onResponse(ResponseListUsers response) {
                        String jsonOutput = GSON.toJson(response);
                        Log.d(LOGGER_TAG, "getMyFollowers: "+jsonOutput);
                        mListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    @Override
    public void getMyFavourites(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        mListener = listener;
        String url = Constants.SNAPS_FAVOURITES+urlEncodeUTF8(params);

        Log.d(LOGGER_TAG, "URL: "+url);
        GsonRequest<ResponseListSnaps> request = new GsonRequest<ResponseListSnaps>(Method.GET, url, ResponseListSnaps.class, null, header,
                new Response.Listener<ResponseListSnaps>() {
                    @Override
                    public void onResponse(ResponseListSnaps response) {
                        String jsonOutput = GSON.toJson(response);
                        Log.d(LOGGER_TAG, "getMyFavourites: "+jsonOutput);
                        mListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    @Override
    public void getFootprints(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        mListener = listener;
        String url = Constants.USER_FOOTPRINTS+urlEncodeUTF8(params);

        Log.d(LOGGER_TAG, "URL: "+url);
        GsonRequest<ResponseFootprints> request = new GsonRequest<ResponseFootprints>(Method.GET, url, ResponseFootprints.class, null, header,
                new Response.Listener<ResponseFootprints>() {
                    @Override
                    public void onResponse(ResponseFootprints response) {
                        String jsonOutput = GSON.toJson(response);
                        Log.d(LOGGER_TAG, "getFootprints: "+jsonOutput);
                        mListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    @Override
    public void getGallery(Map<String, String> params, ApiListener listener) throws Exception {
        mListener = listener;
        String url = Constants.USER_GALLERY+urlEncodeUTF8(params);

        Log.d(LOGGER_TAG, "URL: "+url);
        GsonRequest<ResponseListSnaps> request = new GsonRequest<ResponseListSnaps>(Method.GET, url, ResponseListSnaps.class, null, null,
                new Response.Listener<ResponseListSnaps>() {
                    @Override
                    public void onResponse(ResponseListSnaps response) {
                        String jsonOutput = GSON.toJson(response);
                        Log.d(LOGGER_TAG, "getGallery: "+jsonOutput);
                        mListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    @Override
    public void postFollowUser(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        mListener = listener;
        GsonRequest<ResponseFollowUser> request = new GsonRequest<ResponseFollowUser>(Method.POST, Constants.USER_FOLLOW, ResponseFollowUser.class, params, header,
                new Response.Listener<ResponseFollowUser>() {
                    @Override
                    public void onResponse(ResponseFollowUser response) {
                        String jsonOutput = GSON.toJson(response);
                        Log.d(LOGGER_TAG, "postFollowUser: "+jsonOutput);
                        mListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    @Override
    public void registerDevice(Map<String, String> params, ApiListener listener) throws Exception {
        mListener = listener;
        GsonRequest<BaseResponse> request = new GsonRequest<BaseResponse>(Method.POST, Constants.DEVICE_REGISTER, BaseResponse.class, params, null,
                new Response.Listener<BaseResponse>() {
                    @Override
                    public void onResponse(BaseResponse response) {
                        String jsonOutput = GSON.toJson(response);
                        Log.d(LOGGER_TAG, "registerDevice: "+jsonOutput);
                        mListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    @Override
    public void updateNotification(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        mListener = listener;
        GsonRequest<BaseResponse> request = new GsonRequest<BaseResponse>(Method.POST, Constants.USER_NOTIFICATION, BaseResponse.class, params, header,
                new Response.Listener<BaseResponse>() {
                    @Override
                    public void onResponse(BaseResponse response) {
                        String jsonOutput = GSON.toJson(response);
                        Log.d(LOGGER_TAG, "updateNotification: "+jsonOutput);
                        mListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    @Override
    public void getHotSearchFilters(Map<String, String> params, ApiListener listener, boolean i) throws Exception {
        mListener = listener;
        String url = Constants.BASE_HOT_SEARCH+urlEncodeUTF8(params);

        final boolean isSearch = i;

        Log.d(LOGGER_TAG, "URL: "+url);
        GsonRequest<ResponseHotSearchFilter> request = new GsonRequest<ResponseHotSearchFilter>(Method.GET, url, ResponseHotSearchFilter.class, null, null,
                new Response.Listener<ResponseHotSearchFilter>() {
                    @Override
                    public void onResponse(ResponseHotSearchFilter response) {
                        String jsonOutput = GSON.toJson(response);
                        Log.d(LOGGER_TAG, "getHotSearchFilters: "+jsonOutput);

                        ResponseHotSearchFilter res = GSON.fromJson(jsonOutput, ResponseHotSearchFilter.class);
                        // save into database
                        try {
                            HotSearchFilterDataSource dataSource = new HotSearchFilterDataSource(mContext);
                            dataSource.open();
                            if(dataSource.isTableExist())
                                dataSource.truncateTable();
                            else
                                dataSource.createTable();

                            int show = HotSearch.Show.HIDE.getKey();

                            if(isSearch) // called from hotsearch list view
                                show = HotSearch.Show.SHOW.getKey();

                            int pkId = 1;
                            for(HotSearch hotSearch: res.getMainOptions()) {
                                dataSource.createRow(pkId, hotSearch.getId(), hotSearch.getCategory(), hotSearch.getValue(), hotSearch.getFrom(), hotSearch.getTo(), hotSearch.getType(), show);
                                pkId++;
                            }

                            show = HotSearch.Show.HIDE.getKey();
                            for(HotSearch hotSearch: res.getOtherOptions()) {
                                dataSource.createRow(pkId, hotSearch.getId(), hotSearch.getCategory(), hotSearch.getValue(), hotSearch.getFrom(), hotSearch.getTo(), hotSearch.getType(), show);
                                pkId++;
                            }

                            dataSource.close();
                        } catch (Exception e) {
                            Log.d(LOGGER_TAG, "Error encountered in getHotSearchFilters: "+e.getLocalizedMessage());
                        }

                        mListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    @Override
    public void getSnapsForHomepage(Map<String, String> params, ApiListener listener) throws Exception {
        mListener = listener;
        String url = Constants.SNAPS_POPULAR+urlEncodeUTF8(params);

        Log.d(LOGGER_TAG, "URL: "+url);
        GsonRequest<ResponseSnapsHomepage> request = new GsonRequest<ResponseSnapsHomepage>(Method.GET, url, ResponseSnapsHomepage.class, null, null,
                new Response.Listener<ResponseSnapsHomepage>() {
                    @Override
                    public void onResponse(ResponseSnapsHomepage response) {
                        String jsonOutput = GSON.toJson(response);
                        Log.d(LOGGER_TAG, "getSnapsForHomepage: "+jsonOutput);
                        mListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    @Override
    public void getSnapDetails(Map<String, String> params, ApiListener listener) throws Exception {
        mListener = listener;
        String url = Constants.BASE_SNAPS+urlEncodeUTF8(params);

        Log.d(LOGGER_TAG, "URL: "+url);
        GsonRequest<ResponseSnapDetails> request = new GsonRequest<ResponseSnapDetails>(Method.GET, url, ResponseSnapDetails.class, null, null,
                new Response.Listener<ResponseSnapDetails>() {
                    @Override
                    public void onResponse(ResponseSnapDetails response) {
                        String jsonOutput = GSON.toJson(response);
                        Log.d(LOGGER_TAG, "getSnapDetails: "+jsonOutput);
                        mListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    @Override
    public void getSnapsByFoodHash(Map<String, String> params, ApiListener listener) throws Exception {
        mListener = listener;
        String url = Constants.SNAPS_FOODHASH+urlEncodeUTF8(params);

        Log.d(LOGGER_TAG, "URL: "+url);
        GsonRequest<ResponseListSnaps> request = new GsonRequest<ResponseListSnaps>(Method.GET, url, ResponseListSnaps.class, null, null,
                new Response.Listener<ResponseListSnaps>() {
                    @Override
                    public void onResponse(ResponseListSnaps response) {
                        String jsonOutput = GSON.toJson(response);
                        Log.d(LOGGER_TAG, "getSnapsByFoodHash: "+jsonOutput);
                        mListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    @Override
    public void getSnapsByUser(Map<String, String> params, ApiListener listener) throws Exception {
        mListener = listener;
        String url = Constants.SNAPS_BY_USER+urlEncodeUTF8(params);

        Log.d(LOGGER_TAG, "URL: "+url);
        GsonRequest<ResponseListUsers> request = new GsonRequest<ResponseListUsers>(Method.GET, url, ResponseListUsers.class, null, null,
                new Response.Listener<ResponseListUsers>() {
                    @Override
                    public void onResponse(ResponseListUsers response) {
                        String jsonOutput = GSON.toJson(response);
                        Log.d(LOGGER_TAG, "getSnapsByUser: "+jsonOutput);
                        mListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    @Override
    public void getSnapsFollowings(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        mListener = listener;
        String url = Constants.SNAPS_FOLLOWINGS+urlEncodeUTF8(params);

        Log.d(LOGGER_TAG, "URL: "+url);
        GsonRequest<ResponseListSnaps> request = new GsonRequest<ResponseListSnaps>(Method.GET, url, ResponseListSnaps.class, null, header,
                new Response.Listener<ResponseListSnaps>() {
                    @Override
                    public void onResponse(ResponseListSnaps response) {
                        String jsonOutput = GSON.toJson(response);
                        Log.d(LOGGER_TAG, "getSnapsFollowings: "+jsonOutput);
                        mListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

//    @Override
//    public void postNewSnap(Map<String, String> params, Map<String, List<String>> bodyParams, Map<String, String> header, ApiListener listener) throws Exception {
//        mListener = listener;
//        final Map<String, String> headers = header;
//
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put(Snap.PHOTOS, new JSONArray(bodyParams.get(Snap.PHOTOS)));
//        jsonObject.put(Snap.DISH, new JSONArray(bodyParams.get(Snap.DISH)));
//        jsonObject.put(Snap.FILE_TYPES, new JSONArray(bodyParams.get(Snap.FILE_TYPES)));
//
//        String url = Constants.SNAPS_POST+urlEncodeUTF8(params);
//        JsonObjectRequest request = new JsonObjectRequest(Method.POST,
//                url, jsonObject,
//                new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d(LOGGER_TAG, response.toString());
//                        mListener.onResponse(GSON.fromJson(response.toString(), ResponseBaseWithId.class));
////                        msgResponse.setText(response.toString());
//                    }
//                }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//                mListener.onErrorResponse(error);
//            }
//        }) {
//
//            /**
//             * Passing some request headers
//             * */
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                headers.put("Content-Type", "application/json; charset=utf-8");
//                return headers;
//            }
//
//        };
//
//        request.setRetryPolicy(new DefaultRetryPolicy(
//                MY_SOCKET_TIMEOUT_MS,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
//    }

    @Override
    public void postNewSnap(Map<String, String> params, Map<String, List<String>> bodyParams, Map<String, byte[]> multiPartParams, Map<String, String> header, ApiListener listener) throws Exception {
        mListener = listener;
        final Map<String, String> headers = header;

        String boundary = "snemobile-" + System.currentTimeMillis();
        String mimeType = "multipart/form-data;boundary=" + boundary;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        byte[] multipartBody = null;

        try {

            for(String dish: bodyParams.get(Snap.DISH)) {
                mUtils.buildJsonPart(boundary, dos, Snap.DISH, dish);
            }

            for(String filetype: bodyParams.get(Snap.FILE_TYPES)) {
                mUtils.buildJsonPart(boundary, dos, Snap.FILE_TYPES, filetype);
            }

            // files
            for (Map.Entry<String, byte[]> file: multiPartParams.entrySet()) {
                mUtils.buildFilePart(boundary, dos, Snap.PHOTOS, file.getValue(), file.getKey());
            }

            // send multipart form data necesssary after file data
            dos.writeBytes(mUtils.twoHyphens + boundary + mUtils.twoHyphens + mUtils.lineEnd);
            // pass to multipart body
            multipartBody = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String url = Constants.SNAPS_POST+urlEncodeUTF8(params);

        MultipartRequest request = new MultipartRequest(Method.POST, url, headers, mimeType, multipartBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOGGER_TAG, response.toString());
                        mListener.onResponse(GSON.fromJson(response.toString(), ResponseBaseWithId.class));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    @Override
    public void postReportInappropriate(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        mListener = listener;
        GsonRequest<BaseResponse> request = new GsonRequest<BaseResponse>(Method.POST, Constants.SNAPS_REPORT_INAPPROPRIATE, BaseResponse.class, params, header,
                new Response.Listener<BaseResponse>() {
                    @Override
                    public void onResponse(BaseResponse response) {
                        String jsonOutput = GSON.toJson(response);
                        Log.d(LOGGER_TAG, "postReportInappropriate: "+jsonOutput);
                        mListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    @Override
    public void postSnapLike(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        mListener = listener;
        GsonRequest<ResponsePostLike> request = new GsonRequest<ResponsePostLike>(Method.POST, Constants.SNAPS_LIKE, ResponsePostLike.class, params, header,
                new Response.Listener<ResponsePostLike>() {
                    @Override
                    public void onResponse(ResponsePostLike response) {
                        String jsonOutput = GSON.toJson(response);
                        Log.d(LOGGER_TAG, "postSnapLike: "+jsonOutput);
                        mListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    @Override
    public void postAddSnapToFavourites(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        mListener = listener;
        GsonRequest<BaseResponse> request = new GsonRequest<BaseResponse>(Method.POST, Constants.SNAPS_ADD_FAVE, BaseResponse.class, params, header,
                new Response.Listener<BaseResponse>() {
                    @Override
                    public void onResponse(BaseResponse response) {
                        String jsonOutput = GSON.toJson(response);
                        Log.d(LOGGER_TAG, "postAddSnapToFavourites: "+jsonOutput);
                        mListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    @Override
    public void getRestaurants(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        mListener = listener;
        String url = Constants.RESTAURANT_LIST+urlEncodeUTF8(params);

        Log.d(LOGGER_TAG, "URL: "+url);
        GsonRequest<ResponseRestaurants> request = new GsonRequest<ResponseRestaurants>(Method.GET, url, ResponseRestaurants.class, null, header,
                new Response.Listener<ResponseRestaurants>() {
                    @Override
                    public void onResponse(ResponseRestaurants response) {
                        String jsonOutput = GSON.toJson(response);
                        Log.d(LOGGER_TAG, "getRestaurants: "+jsonOutput);

                        mListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    @Override
    public void getRestaurantDetails(Map<String, String> params, ApiListener listener) throws Exception {
        mListener = listener;
        String url = Constants.BASE_RESTAURANT+urlEncodeUTF8(params);

        Log.d(LOGGER_TAG, "URL: "+url);
        GsonRequest<ResponseRestaurantDetails> request = new GsonRequest<ResponseRestaurantDetails>(Method.GET, url, ResponseRestaurantDetails.class, null, null,
                new Response.Listener<ResponseRestaurantDetails>() {
                    @Override
                    public void onResponse(ResponseRestaurantDetails response) {
                        String jsonOutput = GSON.toJson(response);
                        Log.d(LOGGER_TAG, "getRestaurantDetails: "+jsonOutput);
                        mListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    @Override
    public void postNewRestaurant(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        GsonRequest<ResponseBaseWithId> request = new GsonRequest<ResponseBaseWithId>(Method.POST, Constants.RESTAURANT_ADD, ResponseBaseWithId.class, params, header,
                new Response.Listener<ResponseBaseWithId>() {
                    @Override
                    public void onResponse(ResponseBaseWithId response) {
                        String jsonOutput = GSON.toJson(response);
                        Log.d(LOGGER_TAG, "postNewRestaurant: "+jsonOutput);
                        mListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    @Override
    public void getReasonInappropriate(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        mListener = listener;

        String url = Constants.BASE_REASON_INAPPROPRIATE+urlEncodeUTF8(params);
        GsonRequest<ResponseReasonInappropriate> request = new GsonRequest<ResponseReasonInappropriate>(Method.GET, url, ResponseReasonInappropriate.class, null, header,
                new Response.Listener<ResponseReasonInappropriate>() {
                    @Override
                    public void onResponse(ResponseReasonInappropriate response) {
                        String jsonOutput = GSON.toJson(response);
                        Log.d(LOGGER_TAG, "getReasonInappropriate: "+jsonOutput);
                        mListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    @Override
    public void getFileUploadSetting(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        mListener = listener;

        String url = Constants.BASE_FILE_SETTINGS+urlEncodeUTF8(params);
        GsonRequest<ResponseFileUploadSettings> request = new GsonRequest<ResponseFileUploadSettings>(Method.GET, url, ResponseFileUploadSettings.class, null, header,
                new Response.Listener<ResponseFileUploadSettings>() {
                    @Override
                    public void onResponse(ResponseFileUploadSettings response) {
                        String jsonOutput = GSON.toJson(response);
                        Log.d(LOGGER_TAG, "getFileUploadSetting: " + jsonOutput);
                        mListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mListener.onErrorResponse(error);
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    public String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    private String urlEncodeUTF8(Map<?,?> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?,?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                    urlEncodeUTF8(entry.getKey().toString()),
                    urlEncodeUTF8(entry.getValue().toString())
            ));
        }
        return "?"+sb.toString();
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo networkinfo = cm.getActiveNetworkInfo();
        if (networkinfo != null && networkinfo.isConnected()) {
            return true;
        }
        return false;
    }
}
