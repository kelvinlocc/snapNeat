package com.accordhk.SnapNEat.services;

import com.accordhk.SnapNEat.services.ApiWebServices.ApiListener;

import java.util.List;
import java.util.Map;

/**
 * Created by jm on 22/1/16.
 */
public interface IApi {
    void registerUser(Map<String, String> params, ApiListener listener) throws Exception;
    void loginUser(Map<String, String> params, ApiListener listener) throws Exception;
    void fbloginUser(Map<String, String> params, ApiListener listener) throws Exception;
    void logoutUser(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception;
    void getUserProfile(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception;
//    void updateUserProfile(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception;
    void updateUserProfile(Map<String, String> params, Map<String, byte[]> imageMultiPart, Map<String, String> header, ApiListener listener) throws Exception;
    void changePassword(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception;
    void resetPassword(Map<String, String> params, ApiListener listener) throws Exception;
    void getMyFollowings(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception;
    void getMyFollowers(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception;
    void getMyFavourites(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception;
    void getFootprints(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception;
    void getGallery(Map<String, String> params, ApiListener listener) throws Exception;
    void postFollowUser(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception;

    void registerDevice(Map<String, String> params, ApiListener listener) throws Exception;
    void updateNotification(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception; //User Notification

    void getHotSearchFilters(Map<String, String> params, ApiListener listener, boolean isSearch) throws Exception;
    void getSnapsForHomepage(Map<String, String> params, ApiListener listener) throws Exception;
    void getSnapDetails(Map<String, String> params, ApiListener listener) throws Exception;
    void getSnapsByFoodHash(Map<String, String> params, ApiListener listener) throws Exception;
    void getSnapsByUser(Map<String, String> params, ApiListener listener) throws Exception;
    void getSnapsFollowings(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception;
//    void postNewSnap(Map<String, String> params, Map<String, List<String>> bodyParams, Map<String, String> header, ApiListener listener) throws Exception;
    void postNewSnap(Map<String, String> params, Map<String, List<String>> bodyParams, Map<String, byte[]> multiPartParams, Map<String, String> header, ApiListener listener) throws Exception;
    void postReportInappropriate(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception;
    void postSnapLike(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception;
    void postAddSnapToFavourites(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception;

    void getRestaurants(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception;
    void getRestaurantDetails(Map<String, String> params, ApiListener listener) throws Exception;
    void postNewRestaurant(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception;

    void getReasonInappropriate(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception;

    void getFileUploadSetting(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception;
}
