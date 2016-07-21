package com.accordhk.SnapNEat.services;

import android.util.Log;

import com.accordhk.SnapNEat.dao.HotSearchFilterDataSource;
import com.accordhk.SnapNEat.dao.RestaurantDataSource;
import com.accordhk.SnapNEat.models.BaseResponse;
import com.accordhk.SnapNEat.models.Dish;
import com.accordhk.SnapNEat.models.District;
import com.accordhk.SnapNEat.models.HotSearch;
import com.accordhk.SnapNEat.models.Image;
import com.accordhk.SnapNEat.models.ResponseBaseWithId;
import com.accordhk.SnapNEat.models.ResponseFootprints;
import com.accordhk.SnapNEat.models.ResponseHotSearchFilter;
import com.accordhk.SnapNEat.models.ResponseListSnaps;
import com.accordhk.SnapNEat.models.ResponseListUsers;
import com.accordhk.SnapNEat.models.ResponseLogin;
import com.accordhk.SnapNEat.models.ResponseReasonInappropriate;
import com.accordhk.SnapNEat.models.ResponseRestaurantDetails;
import com.accordhk.SnapNEat.models.ResponseRestaurants;
import com.accordhk.SnapNEat.models.ResponseSnapDetails;
import com.accordhk.SnapNEat.models.ResponseSnapsHomepage;
import com.accordhk.SnapNEat.models.ResponseUserProfile;
import com.accordhk.SnapNEat.models.Restaurant;
import com.accordhk.SnapNEat.models.Snap;
import com.accordhk.SnapNEat.models.Spending;
import com.accordhk.SnapNEat.models.User;
import com.accordhk.SnapNEat.utils.Constants;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by jm on 6/2/16.
 */
public class DummyData extends ApiWebServices {

    private static String LOGGER_TAG = "DummyData";
    private static Gson GSON = new Gson();

    int totalRecords = 23;
    int recordsPerPage = 5;

    public DummyData() {
    }

    @Override
    public void registerUser(Map<String, String> params, ApiListener listener) throws Exception {
        String jsonOutput = "{\"status\":100,\"message\":\"Welcome to SnapNEat\"}";

        Log.d(LOGGER_TAG, "registerUser: "+jsonOutput);

        listener.onResponse(GSON.fromJson(jsonOutput, BaseResponse.class));
    }

    @Override
    public void loginUser(Map<String, String> params, ApiListener listener) throws Exception {
        String jsonOutput = "{\"status\":100,\"message\":\"Welcome to SnapNEat\",\"session_string\":\"sdfksjdflksdfjlskdfsldkfjsdfkljlkjsdflksdflsfd\"}";

        Log.d(LOGGER_TAG, "loginUser: "+jsonOutput);

        listener.onResponse(GSON.fromJson(jsonOutput, ResponseLogin.class));
    }

    @Override
    public void fbloginUser(Map<String, String> params, ApiListener listener) throws Exception {
        String jsonOutput = "{\"status\":100,\"message\":\"Welcome to SnapNEat\",\"session_string\":\"sdfksjdflksdfjlskdfsldkfjsdfkljlkjsdflksdflsfd\"}";

        Log.d(LOGGER_TAG, "fbLoginUser: "+jsonOutput);

        listener.onResponse(GSON.fromJson(jsonOutput, ResponseLogin.class));
    }

    @Override
    public void logoutUser(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        String jsonOutput = "{\"status\":100,\"message\":\"You have successfully logged out from Snap N Eat\"}";

        Log.d(LOGGER_TAG, "logoutUser: "+jsonOutput);

        listener.onResponse(GSON.fromJson(jsonOutput, BaseResponse.class));
    }

    @Override
    public void getUserProfile(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        User user = randomUser();

        ResponseUserProfile data = new ResponseUserProfile();
        data.setStatus(Constants.RES_SUCCESS);
        data.setTotalFollowers(10);
        data.setTotalFollowings(15);
        data.setTotalSnaps(20);
        data.setUserInfo(user);

        String jsonOutput = GSON.toJson(data);

        Log.d(LOGGER_TAG, "getUserProfile: "+jsonOutput);

        listener.onResponse(GSON.fromJson(jsonOutput, ResponseUserProfile.class));
    }

//    @Override
//    public void updateUserProfile(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
//        String jsonOutput = "{\"status\":100,\"message\":\"Register profile successfully updated\"}";
//
//        Log.d(LOGGER_TAG, "updateUserProfile: "+jsonOutput);
//
//        listener.onResponse(GSON.fromJson(jsonOutput, BaseResponse.class));
//    }

    @Override
    public void changePassword(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        String jsonOutput = "{\"status\":100,\"message\":\"Your password has been updated successfully\"}";

        Log.d(LOGGER_TAG, "changePassword: "+jsonOutput);

        listener.onResponse(GSON.fromJson(jsonOutput, BaseResponse.class));
    }

    @Override
    public void resetPassword(Map<String, String> params, ApiListener listener) throws Exception {
        String jsonOutput = "{\"status\":100,\"message\":\"Password has been reset. Please check your email at <EMAIL_ADDRESS> containing the new password.\"}";

        Log.d(LOGGER_TAG, "resetPassword: "+jsonOutput);

        listener.onResponse(GSON.fromJson(jsonOutput, BaseResponse.class));
    }

    @Override
    public void getMyFollowings(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        int page = 1;
        ResponseListUsers data = new ResponseListUsers();
        data.setStatus(Constants.RES_SUCCESS);
        data.setTotalRecords(totalRecords);
        data.setCurrentPageNo(page);
        data.setRecordsPerPage(recordsPerPage);

        List<User> userList = new ArrayList<User>();

        int first = (page-1) * recordsPerPage;
        int last = first + recordsPerPage;

        if(last > totalRecords)
            last = totalRecords;

        for(int x = first; x < last; x++) {
            userList.add(randomUser());
        }

        data.setResults(userList);
        String jsonOutput = GSON.toJson(data);

        Log.d(LOGGER_TAG, "getMyFollowing: "+jsonOutput);

        listener.onResponse(GSON.fromJson(jsonOutput, ResponseListUsers.class));
    }

    @Override
    public void getMyFollowers(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        int page = 1;
        ResponseListUsers data = new ResponseListUsers();
        data.setStatus(Constants.RES_SUCCESS);
        data.setTotalRecords(totalRecords);
        data.setCurrentPageNo(page);
        data.setRecordsPerPage(recordsPerPage);

        List<User> userList = new ArrayList<User>();

        int first = (page-1) * recordsPerPage;
        int last = first + recordsPerPage;

        if(last > totalRecords)
            last = totalRecords;

        for(int x = first; x < last; x++) {
            userList.add(randomUser());
        }

        data.setResults(userList);
        String jsonOutput = GSON.toJson(data);

        Log.d(LOGGER_TAG, "getMyFollowers: "+jsonOutput);

        listener.onResponse(GSON.fromJson(jsonOutput, ResponseListUsers.class));
    }

    @Override
    public void getMyFavourites(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        int page = 1;
        ResponseListSnaps data = new ResponseListSnaps();
        data.setStatus(Constants.RES_SUCCESS);
        data.setTotalRecords(totalRecords);
        data.setCurrentPageNo(page);
        data.setRecordsPerPage(recordsPerPage);

        List<Snap> snapList = new ArrayList<Snap>();
        int first = (page-1) * recordsPerPage;
        int last = first + recordsPerPage;

        if(last > totalRecords)
            last = totalRecords;

        for(int x = first; x < last; x++) {
            snapList.add(randomSnap());
        }

        data.setResults(snapList);

        String jsonOutput = GSON.toJson(data);

        Log.d(LOGGER_TAG, "getMyFavourites: "+jsonOutput);

        listener.onResponse(GSON.fromJson(jsonOutput, ResponseListSnaps.class));
    }

    @Override
    public void getFootprints(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        int page = 1;
        ResponseFootprints data = new ResponseFootprints();
        data.setStatus(Constants.RES_SUCCESS);
        data.setTotalRecords(totalRecords);
        data.setCurrentPageNo(page);
        data.setRecordsPerPage(recordsPerPage);

        List<District> list = new ArrayList<District>();
        int first = (page-1) * recordsPerPage;
        int last = first + recordsPerPage;

        if(last > totalRecords)
            last = totalRecords;

        for(int x = first; x < last; x++) {
            District district = randomDistrict();

            List<Snap> snapList = new ArrayList<Snap>();
            for(int y = 0; y < 3; y++) {
                snapList.add(randomSnap());
            }
            district.setSnaps(snapList);

            list.add(district);
        }

        data.setResults(list);
        String jsonOutput = GSON.toJson(data);

        Log.d(LOGGER_TAG, "getFootprints: "+jsonOutput);

        listener.onResponse(GSON.fromJson(jsonOutput, ResponseFootprints.class));
    }

    @Override
    public void getGallery(Map<String, String> params, ApiListener listener) throws Exception {
        int page = 1;
        ResponseListSnaps data = new ResponseListSnaps();
        data.setStatus(Constants.RES_SUCCESS);
        data.setTotalRecords(totalRecords);
        data.setCurrentPageNo(page);
        data.setRecordsPerPage(recordsPerPage);

        List<Snap> list = new ArrayList<Snap>();

        int first = (page-1) * recordsPerPage;
        int last = first + recordsPerPage;

        if(last > totalRecords)
            last = totalRecords;

        for(int x = first; x < last; x++) {
            Snap snap = randomSnap();
            snap.setDistrict(randomDistrict());
            list.add(snap);
        }

        data.setResults(list);
        String jsonOutput = GSON.toJson(data);

        Log.d(LOGGER_TAG, "getGallery: "+jsonOutput);

        listener.onResponse(GSON.fromJson(jsonOutput, ResponseListSnaps.class));
    }

    @Override
    public void postFollowUser(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        String jsonOutput = "{\"status\":100,\"message\":\"Successfully followed user.\"}";

        Log.d(LOGGER_TAG, "postFollowUser: "+jsonOutput);

        listener.onResponse(GSON.fromJson(jsonOutput, BaseResponse.class));
    }

    @Override
    public void registerDevice(Map<String, String> params, ApiListener listener) throws Exception {
        String jsonOutput = "{\"status\":100,\"message\":\"Device is successfully registered.\"}";

        Log.d(LOGGER_TAG, "registerDevice: "+jsonOutput);

        listener.onResponse(GSON.fromJson(jsonOutput, BaseResponse.class));
    }

    @Override
    public void updateNotification(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        String jsonOutput = "{\"status\":100,\"message\":\"Device setting updated successfully.\"}";

        Log.d(LOGGER_TAG, "updateDevice: "+jsonOutput);

        listener.onResponse(GSON.fromJson(jsonOutput, BaseResponse.class));
    }

    @Override
    public void getHotSearchFilters(Map<String, String> params, ApiListener listener, boolean isSearch) throws Exception {
        ResponseHotSearchFilter data = new ResponseHotSearchFilter();
        data.setStatus(Constants.RES_SUCCESS);

        List<HotSearch> results = new ArrayList<HotSearch>();

        HotSearch hs = new HotSearch();
        hs.setId(1);
        hs.setCategory(HotSearch.Category.DISTRICT.getKey());
        hs.setValue("上環");
        hs.setType(HotSearch.Type.SELECT.getKey());
        results.add(hs);

        hs = new HotSearch();
        hs.setId(2);
        hs.setCategory(HotSearch.Category.DISTRICT.getKey());
        hs.setValue("大坑");
        hs.setType(HotSearch.Type.SELECT.getKey());
        results.add(hs);

        hs = new HotSearch();
        hs.setId(3);
        hs.setCategory(HotSearch.Category.DISTRICT.getKey());
        hs.setValue("山頂");
        hs.setType(HotSearch.Type.SELECT.getKey());
        results.add(hs);

        hs = new HotSearch();
        hs.setId(4);
        hs.setCategory(HotSearch.Category.DISTRICT.getKey());
        hs.setValue("中環");
        hs.setType(HotSearch.Type.SELECT.getKey());
        results.add(hs);

        hs = new HotSearch();
        hs.setId(6);
        hs.setCategory(HotSearch.Category.DISH.getKey());
        hs.setValue("中式");
        hs.setType(HotSearch.Type.SELECT.getKey());
        results.add(hs);

        hs = new HotSearch();
        hs.setId(7);
        hs.setCategory(HotSearch.Category.DISH.getKey());
        hs.setValue("中菜");
        hs.setType(HotSearch.Type.SELECT.getKey());
        results.add(hs);

        hs = new HotSearch();
        hs.setId(8);
        hs.setCategory(HotSearch.Category.DISH.getKey());
        hs.setValue("印尼菜");
        hs.setType(HotSearch.Type.SELECT.getKey());
        results.add(hs);

        hs = new HotSearch();
        hs.setId(9);
        hs.setCategory(HotSearch.Category.DISH.getKey());
        hs.setValue("印度菜");
        hs.setType(HotSearch.Type.SELECT.getKey());
        results.add(hs);

        hs = new HotSearch();
        hs.setId(11);
        hs.setCategory(HotSearch.Category.SPENDINGS.getKey());
        hs.setValue("50以下");
        hs.setFrom(0F);
        hs.setTo(50F);
        hs.setType(HotSearch.Type.RANGE.getKey());
        results.add(hs);

        hs = new HotSearch();
        hs.setId(12);
        hs.setCategory(HotSearch.Category.SPENDINGS.getKey());
        hs.setValue("51-100");
        hs.setFrom(51F);
        hs.setTo(100F);
        hs.setType(HotSearch.Type.RANGE.getKey());
        results.add(hs);

        hs = new HotSearch();
        hs.setId(13);
        hs.setCategory(HotSearch.Category.SPENDINGS.getKey());
        hs.setValue("101-200");
        hs.setFrom(101F);
        hs.setTo(200F);
        hs.setType(HotSearch.Type.RANGE.getKey());
        results.add(hs);

        hs = new HotSearch();
        hs.setId(14);
        hs.setCategory(HotSearch.Category.SPENDINGS.getKey());
        hs.setValue("201-400");
        hs.setFrom(201F);
        hs.setTo(400F);
        hs.setType(HotSearch.Type.RANGE.getKey());

        hs = new HotSearch();
        hs.setId(17);
        hs.setCategory(HotSearch.Category.HASHTAGS.getKey());
        hs.setValue("#蠔吧");
        hs.setType(HotSearch.Type.SELECT.getKey());
        results.add(hs);

        hs = new HotSearch();
        hs.setId(18);
        hs.setCategory(HotSearch.Category.HASHTAGS.getKey());
        hs.setValue("#傳統平安包");
        hs.setType(HotSearch.Type.SELECT.getKey());
        results.add(hs);

        hs = new HotSearch();
        hs.setId(19);
        hs.setCategory(HotSearch.Category.HASHTAGS.getKey());
        hs.setValue("#奶醬");
        hs.setType(HotSearch.Type.SELECT.getKey());
        results.add(hs);

        hs = new HotSearch();
        hs.setId(20);
        hs.setCategory(HotSearch.Category.HASHTAGS.getKey());
        hs.setValue("#平安包");
        hs.setType(HotSearch.Type.SELECT.getKey());
        results.add(hs);

        hs = new HotSearch();
        hs.setId(21);
        hs.setCategory(HotSearch.Category.HASHTAGS.getKey());
        hs.setValue("#爐端燒");
        hs.setType(HotSearch.Type.SELECT.getKey());
        results.add(hs);

        hs = new HotSearch();
        hs.setId(22);
        hs.setCategory(HotSearch.Category.HASHTAGS.getKey());
        hs.setValue("#竹昇麵");
        hs.setType(HotSearch.Type.SELECT.getKey());
        results.add(hs);

        hs = new HotSearch();
        hs.setId(23);
        hs.setCategory(HotSearch.Category.HASHTAGS.getKey());
        hs.setValue("#紅酒");
        hs.setType(HotSearch.Type.SELECT.getKey());
        results.add(hs);

        hs = new HotSearch();
        hs.setId(24);
        hs.setCategory(HotSearch.Category.HASHTAGS.getKey());
        hs.setValue("#茶餐廳");
        hs.setType(HotSearch.Type.SELECT.getKey());
        results.add(hs);

        data.setMainOptions(results);

        // for other options
        results = new ArrayList<HotSearch>();

        hs = new HotSearch();
        hs.setId(5);
        hs.setCategory(HotSearch.Category.DISTRICT.getKey());
        hs.setValue("蘇豪");
        hs.setType(HotSearch.Type.SELECT.getKey());
        results.add(hs);

        hs = new HotSearch();
        hs.setId(10);
        hs.setCategory(HotSearch.Category.DISH.getKey());
        hs.setValue("台灣");
        hs.setType(HotSearch.Type.SELECT.getKey());
        results.add(hs);

        hs = new HotSearch();
        hs.setId(15);
        hs.setCategory(HotSearch.Category.SPENDINGS.getKey());
        hs.setValue("401-800");
        hs.setFrom(401F);
        hs.setTo(800F);
        hs.setType(HotSearch.Type.RANGE.getKey());
        results.add(hs);

        hs = new HotSearch();
        hs.setId(16);
        hs.setCategory(HotSearch.Category.SPENDINGS.getKey());
        hs.setValue("800以上");
        hs.setFrom(800F);
        hs.setTo(0F);
        hs.setType(HotSearch.Type.RANGE.getKey());
        results.add(hs);

        hs = new HotSearch();
        hs.setId(25);
        hs.setCategory(HotSearch.Category.HASHTAGS.getKey());
        hs.setValue("#點心");
        hs.setType(HotSearch.Type.SELECT.getKey());
        results.add(hs);

        data.setOtherOptions(results);
        String jsonOutput = GSON.toJson(data);

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

            int show;
            int pkId = 1;
            for(HotSearch hotSearch: res.getMainOptions()) {
                show = HotSearch.Show.SHOW.getKey();
                dataSource.createRow(pkId, hotSearch.getId(), hotSearch.getCategory(), hotSearch.getValue(), hotSearch.getFrom(), hotSearch.getTo(), hotSearch.getType(), show);
                pkId++;
            }

            for(HotSearch hotSearch: res.getOtherOptions()) {
                show = HotSearch.Show.HIDE.getKey();
                dataSource.createRow(pkId, hotSearch.getId(), hotSearch.getCategory(), hotSearch.getValue(), hotSearch.getFrom(), hotSearch.getTo(), hotSearch.getType(), show);
                pkId++;
            }

            dataSource.close();
        } catch (Exception e) {
            Log.d(LOGGER_TAG, "Error encountered in getHotSearchFilters: "+e.getLocalizedMessage());
        }

        listener.onResponse(res);
    }

    @Override
    public void getSnapsForHomepage(Map<String, String> params, ApiListener listener) throws Exception {
        List<Snap> snaps = new ArrayList<Snap>();
        Snap snap;

//        for(int x = 0; x<5; x++) {
//            snap = randomSnap();
//            snap.setDistrict(randomDistrict());
//            snap.setUser(randomUser());
//            snaps.add(snap);
//        }

        ResponseSnapsHomepage data = new ResponseSnapsHomepage();
        data.setStatus(Constants.RES_SUCCESS);
        data.setMessage("test no more snaps");
        data.setResults(snaps);

        String jsonOutput = GSON.toJson(data);

        Log.d(LOGGER_TAG, "getSnapsForHomepage: "+jsonOutput);

        Log.d(LOGGER_TAG, "END getSnapsForHomepage");
        listener.onResponse(GSON.fromJson(jsonOutput, ResponseSnapsHomepage.class));
    }

    @Override
    public void getSnapDetails(Map<String, String> params, ApiListener listener) throws Exception {
        Snap snap = randomSnap();

        List<Image> images = new ArrayList<Image>();
        List<String> names = new ArrayList<String>();
        Image image;
        for(int x = 0; x<5; x++) {
            image = randomImage();
            images.add(image);
            names.add(image.getImageThumbnail());
        }

        snap.setPhotos(names);
        snap.setDistrict(randomDistrict());
        snap.setSpending(randomSpending());

        List<Dish> dish = new ArrayList<Dish>();
        dish.add(randomDish());
        dish.add(randomDish());

        snap.setDishes(dish);
        snap.setRestaurant(randomRestaurant());
        snap.setUser(randomUser());

        List<Snap> relatedSnaps = new ArrayList<Snap>();
        //Related snaps
        Snap relatedSnap;
        for(int x = 0; x<5; x++) {
            names = new ArrayList<>();
            relatedSnap = randomSnap();
            images = new ArrayList<Image>();
            for(int y = 0; y<5; y++) {
                image = randomImage();
                images.add(image);
                names.add(image.getImage());
            }

            relatedSnap.setPhotos(names);
            relatedSnaps.add(relatedSnap);
        }

        snap.setRelatedSnaps(relatedSnaps);

        ResponseSnapDetails data = new ResponseSnapDetails();
        data.setStatus(Constants.RES_SUCCESS);
        data.setResults(snap);

        String jsonOutput = GSON.toJson(data);

        Log.d(LOGGER_TAG, "getSnapDetails: "+jsonOutput);

        listener.onResponse(GSON.fromJson(jsonOutput, ResponseSnapDetails.class));
    }

    @Override
    public void getSnapsByFoodHash(Map<String, String> params, ApiListener listener) throws Exception {
        int page = 1;

//        for(NameValuePair x: nameValuePairs){
//            if(x.getName().equals(Constants.STR_PAGE)) {
//                page = Integer.parseInt(x.getValue());
//            }
//        }

        if(params.containsKey(Constants.PARAM_PAGE)) {
            page = Integer.parseInt(params.get(Constants.PARAM_PAGE));
        }

        List<Snap> snapList = new ArrayList<Snap>();
        int first = (page-1) * recordsPerPage;
        int last = first + recordsPerPage;

        if(last > totalRecords)
            last = totalRecords;

        for(int x = first; x < last; x++) {
            Snap snap = randomSnap();
            snap.setUser(randomUser());
            snapList.add(snap);
        }

        ResponseListSnaps data = new ResponseListSnaps();
        data.setStatus(Constants.RES_SUCCESS);
        data.setTotalRecords(totalRecords);
        data.setCurrentPageNo(page);
        data.setRecordsPerPage(recordsPerPage);
        data.setResults(snapList);

        String jsonOutput = GSON.toJson(data);

        Log.d(LOGGER_TAG, "getSnapsByFoodHash: "+jsonOutput);

        listener.onResponse(GSON.fromJson(jsonOutput, ResponseListSnaps.class));
    }

    @Override
    public void getSnapsByUser(Map<String, String> params, ApiListener listener) throws Exception {
        int page = 1;

//        for(NameValuePair x: nameValuePairs){
//            if(x.getName().equals(Constants.STR_PAGE)) {
//                page = Integer.parseInt(x.getValue());
//            }
//        }

        if(params.containsKey(Constants.PARAM_PAGE)) {
            page = Integer.parseInt(params.get(Constants.PARAM_PAGE));
        }

        List<User> userList = new ArrayList<User>();
        int first = (page-1) * recordsPerPage;
        int last = first + recordsPerPage;

        if(last > totalRecords)
            last = totalRecords;

        for(int x = first; x < last; x++) {
            User user = randomUser();

            List<Snap> snapList = new ArrayList<Snap>();
            for (int y = 0; y<3;y++){
                snapList.add(randomSnap());
            }

            user.setSnaps(snapList);
            userList.add(user);
        }

        ResponseListUsers data = new ResponseListUsers();
        data.setStatus(Constants.RES_SUCCESS);
        data.setTotalRecords(totalRecords);
        data.setCurrentPageNo(page);
        data.setRecordsPerPage(recordsPerPage);
        data.setResults(userList);

        String jsonOutput = GSON.toJson(data);

        Log.d(LOGGER_TAG, "getSnapsByUser: "+jsonOutput);

        listener.onResponse(GSON.fromJson(jsonOutput, ResponseListUsers.class));
    }

    @Override
    public void getSnapsFollowings(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        int page = 1;
        List<Snap> snapList = new ArrayList<Snap>();
        int first = (page-1) * recordsPerPage;
        int last = first + recordsPerPage;

        if(last > totalRecords)
            last = totalRecords;

        for(int x = first; x < last; x++) {
            Snap snap = randomSnap();
            snap.setUser(randomUser());
            snapList.add(snap);
        }

        ResponseListSnaps data = new ResponseListSnaps();
        data.setStatus(Constants.RES_SUCCESS);
        data.setTotalRecords(totalRecords);
        data.setCurrentPageNo(page);
        data.setRecordsPerPage(recordsPerPage);
        data.setResults(snapList);

        String jsonOutput = GSON.toJson(data);

        Log.d(LOGGER_TAG, "getSnapsFollowings: "+jsonOutput);

        listener.onResponse(GSON.fromJson(jsonOutput, ResponseListSnaps.class));
    }

//    @Override
//    public void postNewSnap(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
//        String jsonOutput = "{\"status\":100,\"message\":\"Successfully posted snap.\", \"id\":1}";
//
//        Log.d(LOGGER_TAG, "postNewSnap: "+jsonOutput);
//
//        listener.onResponse(GSON.fromJson(jsonOutput, ResponseBaseWithId.class));
//    }

    @Override
    public void postReportInappropriate(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        String jsonOutput = "{\"status\":100,\"message\":\"Post report inappropriate success.\"}";

        Log.d(LOGGER_TAG, "postReportInappropriate: "+jsonOutput);

        listener.onResponse(GSON.fromJson(jsonOutput, BaseResponse.class));
    }

    @Override
    public void postSnapLike(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        String jsonOutput = "{\"status\":100,\"message\":\"Snap like success.\"}";

        Log.d(LOGGER_TAG, "postSnapLike: "+jsonOutput);

        listener.onResponse(GSON.fromJson(jsonOutput, BaseResponse.class));
    }

    @Override
    public void postAddSnapToFavourites(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        String jsonOutput = "{\"status\":100,\"message\":\"Snap added to favourites successfully.\"}";

        Log.d(LOGGER_TAG, "postAddSnapToFavourites: "+jsonOutput);

        listener.onResponse(GSON.fromJson(jsonOutput, BaseResponse.class));
    }

    @Override
    public void getRestaurants(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        int page = 1;
        ResponseRestaurants data = new ResponseRestaurants();
        data.setStatus(Constants.RES_SUCCESS);
        data.setTotalRecords(totalRecords);
        data.setCurrentPageNo(page);
        data.setRecordsPerPage(recordsPerPage);

        List<Restaurant> list = new ArrayList<Restaurant>();
        int first = (page-1) * recordsPerPage;
        int last = first + recordsPerPage;

        if(last > totalRecords)
            last = totalRecords;

        for(int x = first; x < last; x++) {
            list.add(randomRestaurant());
        }

        data.setResults(list);

        String jsonOutput = GSON.toJson(data);

        Log.d(LOGGER_TAG, "getRestaurants: "+jsonOutput);

        ResponseRestaurants res = GSON.fromJson(jsonOutput, ResponseRestaurants.class);

        try {
            RestaurantDataSource dataSource = new RestaurantDataSource(mContext);
            dataSource.open();
            if(dataSource.isTableExist())
                dataSource.truncateTable();
            else
                dataSource.createTable();

            for (int x = 0; x < res.getResults().size(); x++) {
                Restaurant restaurant = res.getResults().get(x);
                dataSource.createRow(restaurant.getId(), restaurant.getName(), restaurant.getLocation(), restaurant.getLatitude(), restaurant.getLongitude());
            }

            dataSource.close();

        } catch (Exception e) {
            Log.d(LOGGER_TAG, "Error encountered while trying to save Restaurants in DB: "+e.getLocalizedMessage());
        }

        listener.onResponse(res);
    }

    @Override
    public void getRestaurantDetails(Map<String, String> params, ApiListener listener) throws Exception {
        ResponseRestaurantDetails data = new ResponseRestaurantDetails();
        data.setStatus(Constants.RES_SUCCESS);

        Restaurant restaurant = randomRestaurant();

        List<Image> images = new ArrayList<Image>();
        for(int x = 0; x < 5; x++) {
            images.add(randomImage());
        }
        restaurant.setPhotos(images);

        List<Snap> relatedSnaps = new ArrayList<Snap>();
        //Related snaps
        Snap relatedSnap;
        for(int x = 0; x<5; x++) {
            relatedSnap = randomSnap();
            relatedSnaps.add(relatedSnap);
        }

        restaurant.setRestoFood(relatedSnaps);

        List<Dish> dishes = new ArrayList<Dish>();
        for(int x = 0; x < 5; x++) {
            dishes.add(randomDish());
        }
        restaurant.setDishes(dishes);
        restaurant.setSpending(randomSpending());

        data.setRestaurantInfo(restaurant);

        String jsonOutput = GSON.toJson(data);

        Log.d(LOGGER_TAG, "getRestaurantDetails: "+jsonOutput);

        listener.onResponse(GSON.fromJson(jsonOutput, ResponseRestaurantDetails.class));
    }

    @Override
    public void postNewRestaurant(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        String jsonOutput = "{\"status\":100,\"message\":\"Successfully added restaurant.\", \"id\":1}";

        Log.d(LOGGER_TAG, "postNewRestaurant: "+jsonOutput);

        listener.onResponse(GSON.fromJson(jsonOutput, ResponseBaseWithId.class));
    }

    @Override
    public void getReasonInappropriate(Map<String, String> params, Map<String, String> header, ApiListener listener) throws Exception {
        String jsonOutput = "{\"status\":100,\"message\":\"Successfully added restaurant.\"," +
                "\"results\":[{\"id\":1,\"name\":\"Reason 1\"}," +
                "{\"id\":2,\"name\":\"Reason 2\"}," +
                "{\"id\":3,\"name\":\"Reason 3\"}," +
                "{\"id\":4,\"name\":\"Reason 4\"}," +
                "{\"id\":5,\"name\":\"Reason 5\"}]}";

        Log.d(LOGGER_TAG, "getReasonInappropriate: "+jsonOutput);

        listener.onResponse(GSON.fromJson(jsonOutput, ResponseReasonInappropriate.class));
    }

    public String[] avatars = {"http://i.ebayimg.com/00/s/NzY4WDEwMjQ=/z/xsEAAOSwd4tTvrax/$_12.JPG?set_id=880000500F",
            "http://i.ebayimg.com/00/s/ODcxWDU4MA==/z/A6AAAOSwqu9U7mL~/$_1.JPG?set_id=880000500F",
            "https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcTmHuCaF18zu22QmrnEPS-w3LwiFoAWTy5-_7UrKCl1kM5AhFkL",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQZZsrQJ-EjlpGpQETSzrzbRUILNSJLGoa0S5Zx9vh0jQTY0gFC",
            "https://pbs.twimg.com/media/CasWPWFXIAAslTN.jpg",
            "http://bnh3n1wwq9a4femwm1jovhxkcm.wpengine.netdna-cdn.com/wp-content/uploads/2012/03/little-girl-float.jpg",
            "http://g02.a.alicdn.com/kf/HTB16UnEHVXXXXX6XFXXq6xXFXXXW/New-strange-toy-Whole-people-pirate-barrels-classical-random-game-gathering-toys-Large-and-small-size.jpg",
            "http://www.japantrendshop.com/images/hatsune-miku-pop-up-pirate-game-toy-th.jpg",
            "http://tattoooz.com/wp-content/uploads/2013/05/Small-Tattoo-for-Women-8.jpg"};
//            {"http://www.jewellery-photographer.com.au/images/Portfolio/Thumbnails/jewellery-models083-thumbnail.jpg",
//            "http://cairnpacific.com/wp-content/uploads/2015/03/Tom-bio-thumbnail.jpg",
//            "https://www.elsevier.com/__data/assets/image/0003/138423/Eleanora-Palmero-thumb.jpg",
//            "http://www.spitenet.com/origami/gallery/Quilling/People-3back-w.jpg",
//            "http://rack.1.mshcdn.com/media/ZgkyMDE1LzAyLzIzLzE3L09zY2Fyc1JlZENhLmZlODk0LmpwZwpwCXRodW1iCTU3NXgzMjMjCmUJanBn/f29b00cf/306/Oscars-Red-Carpet-Thumbnail-02.jpg",
//            "http://www.jwt.com/blog/wp-content/uploads/2013/11/mw-thumbnail--300x246.png",
//            "http://hecktictravels.com/wp-content/uploads/2014/06/Maui-Snorkeling-THUMBNAIL.jpg",
//            "http://www.herworldplus.com/sites/default/files/toxic%20party%20people%20thumbnail.jpg",
//            "http://www.thepaleomom.com/wp-content/uploads/2015/08/thumbnail-300x200.jpg"};

    public String[] avatarThumbs = {"http://i.ebayimg.com/00/s/NzY4WDEwMjQ=/z/xsEAAOSwd4tTvrax/$_12.JPG?set_id=880000500F",
            "http://i.ebayimg.com/00/s/ODcxWDU4MA==/z/A6AAAOSwqu9U7mL~/$_1.JPG?set_id=880000500F",
            "https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcTmHuCaF18zu22QmrnEPS-w3LwiFoAWTy5-_7UrKCl1kM5AhFkL",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQZZsrQJ-EjlpGpQETSzrzbRUILNSJLGoa0S5Zx9vh0jQTY0gFC",
            "https://pbs.twimg.com/media/CasWPWFXIAAslTN.jpg",
            "http://bnh3n1wwq9a4femwm1jovhxkcm.wpengine.netdna-cdn.com/wp-content/uploads/2012/03/little-girl-float.jpg",
            "http://g02.a.alicdn.com/kf/HTB16UnEHVXXXXX6XFXXq6xXFXXXW/New-strange-toy-Whole-people-pirate-barrels-classical-random-game-gathering-toys-Large-and-small-size.jpg",
            "http://www.japantrendshop.com/images/hatsune-miku-pop-up-pirate-game-toy-th.jpg",
            "http://tattoooz.com/wp-content/uploads/2013/05/Small-Tattoo-for-Women-8.jpg"};
//            {"http://www.jewellery-photographer.com.au/images/Portfolio/Thumbnails/jewellery-models083-thumbnail.jpg",
//            "http://cairnpacific.com/wp-content/uploads/2015/03/Tom-bio-thumbnail.jpg",
//            "https://www.elsevier.com/__data/assets/image/0003/138423/Eleanora-Palmero-thumb.jpg",
//            "http://www.spitenet.com/origami/gallery/Quilling/People-3back-w.jpg",
//            "http://rack.1.mshcdn.com/media/ZgkyMDE1LzAyLzIzLzE3L09zY2Fyc1JlZENhLmZlODk0LmpwZwpwCXRodW1iCTU3NXgzMjMjCmUJanBn/f29b00cf/306/Oscars-Red-Carpet-Thumbnail-02.jpg",
//            "http://www.jwt.com/blog/wp-content/uploads/2013/11/mw-thumbnail--300x246.png",
//            "http://hecktictravels.com/wp-content/uploads/2014/06/Maui-Snorkeling-THUMBNAIL.jpg",
//            "http://www.herworldplus.com/sites/default/files/toxic%20party%20people%20thumbnail.jpg",
//            "http://www.thepaleomom.com/wp-content/uploads/2015/08/thumbnail-300x200.jpg"};

    public String[] snapImages = {"http://static01.nyt.com/images/2012/10/31/dining/31STUFF_APPLES/31STUFF_APPLES-articleLarge-v2.jpg",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRvz_a-c3R1l5GTLOmUoicib2WlNaRdfVimTmpwCKou_T9teExy",
            "https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcQaGxVb957GxTW8Ddj04mz20u8HHH3FxOidJF6Z3PL4EYswcPjr",
            "http://cbsnews1.cbsistatic.com/hub/i/r/2012/04/12/a2881043-8ba5-11e2-9400-029118418759/resize/620x465/b28c482f68f8fe86a3b9e7ad4d4a8988/SmallPortion.jpg",
            "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcQotB7mSoLjWCWqMIMvNUimhABF5Pf3fNyOPnMoCPQbwgg6Pe-U",
            "https://s-media-cache-ak0.pinimg.com/736x/1f/d5/62/1fd562062d224602fc529fdae418936b.jpg",
            "http://thetinydiner.com/sites/default/files/goodsize.jpg",
            "http://www.philipsmall.co.uk/flash/food3.jpg",
            "http://cdn.vogue.com.au/media/articles/1/2/8/0/12823-1_n.jpg?144408"};

    public String[] snapImageThumbs = {"http://static01.nyt.com/images/2012/10/31/dining/31STUFF_APPLES/31STUFF_APPLES-articleLarge-v2.jpg",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRvz_a-c3R1l5GTLOmUoicib2WlNaRdfVimTmpwCKou_T9teExy",
            "https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcQaGxVb957GxTW8Ddj04mz20u8HHH3FxOidJF6Z3PL4EYswcPjr",
            "http://cbsnews1.cbsistatic.com/hub/i/r/2012/04/12/a2881043-8ba5-11e2-9400-029118418759/resize/620x465/b28c482f68f8fe86a3b9e7ad4d4a8988/SmallPortion.jpg",
            "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcQotB7mSoLjWCWqMIMvNUimhABF5Pf3fNyOPnMoCPQbwgg6Pe-U",
            "https://s-media-cache-ak0.pinimg.com/736x/1f/d5/62/1fd562062d224602fc529fdae418936b.jpg",
            "http://thetinydiner.com/sites/default/files/goodsize.jpg",
            "http://www.philipsmall.co.uk/flash/food3.jpg",
            "http://cdn.vogue.com.au/media/articles/1/2/8/0/12823-1_n.jpg?144408"};
//            {"https://www.lionworldtravel.com/sites/default/files/12A-Azure-Food-thumbnail.jpg",
//            "http://www.herworldplus.com/sites/default/files/imagecache/node-story-main-image/food%20THUMBNAIL.jpg",
//            "http://www.muscleandfitness.com/sites/muscleandfitness.com/files/styles/nodes_next_previous_155x89/public/Holiday-Nutrition.jpg?itok=tz1lcIKJ",
//            "http://www.insightsurvey.co.za/wp-content/uploads/2015/09/Fast-Food-Thumbnail-130x130.jpg",
//            "http://www.muscleandfitness.com/sites/muscleandfitness.com/files/styles/nodes_next_previous_155x89/public/media/balanced-diet-1.jpg?itok=af3EDUMD",
//            "http://cdn.sandiegouniontrib.com/img/photos/2014/12/30/ec_food341775x001_r300x220.jpg?8da35fc3540e114a3306dd50fc7f4404bbc3d28f",
//            "https://lh3.googleusercontent.com/SVoyD0YPb2q4MjMhS4Lx0ZaHXasFfZfkcMMEZJ_jwrJlc9tPA7AhTTHkQXVEpsbo7g=h310",
//            "http://www.fpalliance.com/images/default-source/Pet-Food/thumbnail-pet-food.jpg?sfvrsn=2",
//            "http://cdn.lifestyle.com.au/cache/206x103/Recipes/Thumbnails/food-source-np_rhubarb-tart.jpg"};

    public String[] title = {"四川","新加坡菜","瑞士菜","泰國菜","越南菜","西方","上海","地中海","西班牙菜","新疆菜"};
    public String[] districtName = {"中式","中菜","印尼菜","印度菜","台灣","台灣菜","四川","多國菜","山東","川菜"};

    public String[] emails = {"3c8zbsi6oxz_xg@60mi1ivv9.com","5duba.4c7pely@8ziuy52.com","c3slmciek@ah4bh34jwhn.com","lxc8j9w41ztg_6@hg9rgejhmba.com","h_ubjeddo.@zeymw5kpbr.com","yr58568@oqcm-dwdh.com","hpw83h1ax2s-@ieoa0h55v.com","-j_ung@i4nwoimv8nhw.com","7_v7-3z4m0yjf@w3r3-k.com","1n-k@jlr6fwzdft.com"};

    private Snap randomSnap() {
        Snap snap = new Snap();

        snap.setId(randomInt(100));
        snap.setTitle(title[randomInt(9)]);
        snap.setImage(snapImages[randomInt(9)]);
        snap.setImageThumbnail(snapImageThumbs[randomInt(9)]);
        snap.setRating(randomInt(5));
        snap.setTotalLikes(randomInt(1000));
        snap.setComments(randomString(30, Mode.ALPHA));

        return snap;
    }

    private District randomDistrict() {
        District district = new District();

        double longitude = Math.random() * Math.PI * 2;
        double latitude = Math.acos(Math.random() * 2 - 1);

        district.setId(randomInt(100));
        district.setName(districtName[randomInt(9)]);
        district.setLatitude((float)latitude);
        district.setLongitude((float)longitude);

        return district;
    }

    private User randomUser() {
        User user = new User();

//        user.setId(randomInt(100));
        user.setId(randomInt(3));
        user.setUsername(randomString(15, Mode.ALPHA));
        user.setEmail(emails[randomInt(9)]);
        user.setAvatar(avatars[randomInt(9)]);
        user.setAvatarThumbnail(avatarThumbs[randomInt(9)]);

        return user;
    }

    private Spending randomSpending() {
        Spending spending = new Spending();

        spending.setId(randomInt(1000));
        spending.setCategory(randomInt(5));
        spending.setType(2);
        spending.setValue(String.valueOf(randomInt(10))+"-"+String.valueOf(randomInt(100)));
//        spending.setFrom(randomInt(10));
//        spending.setTo(randomInt(800));

        return spending;
    }

    private Dish randomDish() {
        Dish dish = new Dish();

        dish.setId(randomInt(100));
        dish.setName(randomString(10, Mode.ALPHA));

        return dish;
    }

    private Restaurant randomRestaurant() {
        Restaurant restaurant = new Restaurant();

        restaurant.setId(randomInt(6));
        restaurant.setName(randomString(20, Mode.ALPHA));
        restaurant.setLocation(randomString(40, Mode.ALPHA));
        restaurant.setLatitude(0.0f);
        restaurant.setLongitude(0.0f);

        return restaurant;
    }

    private Image randomImage() {
        Image image = new Image();

        image.setId(randomInt(1000));
        image.setImage(snapImages[randomInt(9)]);
        image.setImageThumbnail(snapImageThumbs[randomInt(9)]);

        return image;
    }

    private int randomInt(int max) {
        Random random = new Random();
        int num = random.nextInt(max);

        if(num == 0)
            num = 1;

        return num;
    }

    public enum Mode {
        ALPHA, ALPHANUMERIC, NUMERIC;
    }

    private String randomString(int length, Mode mode) {
        StringBuffer buffer = new StringBuffer();
        String characters = "";

        switch (mode) {
            case ALPHA:
                characters = " abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
                break;

            case ALPHANUMERIC:
                characters = " abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
                break;

            case NUMERIC:
                characters = "1234567890";
                break;
        }

        int charactersLength = characters.length();

        for (int i = 0; i < length; i++) {
            double index = Math.random() * charactersLength;
            buffer.append(characters.charAt((int) index));
        }

        return buffer.toString();
    }
}
