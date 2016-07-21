package com.accordhk.SnapNEat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.accordhk.SnapNEat.models.Dish;
import com.accordhk.SnapNEat.models.ResponseFileUploadSettings;
import com.accordhk.SnapNEat.models.Restaurant;
import com.accordhk.SnapNEat.models.Spending;
import com.accordhk.SnapNEat.models.User;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jm on 26/1/16.
 */
public class SharedPref {
    private static final String APP_PREF = "com.accordhkdev.snapneat";
    private SharedPreferences mSharedPreferences;
    private Editor mEditor;
    private static Gson GSON = new Gson();

    private static final String SEL_LANGUAGE = "selLanguage";

    public SharedPref(Context context) {
        this.mSharedPreferences = context.getSharedPreferences(APP_PREF, Activity.MODE_PRIVATE);
        this.mEditor = mSharedPreferences.edit();
    }

    public String getPrefString(String key) {
        return mSharedPreferences.getString(key, "");
    }

    public int getPrefInt(String key) {
        int num = -1;

        try {
            return Integer.parseInt(mSharedPreferences.getString(key, ""));
        } catch (Exception e){}

        return num;
    }

    public boolean getPrefBoolean(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }

    public double getPrefDouble(String key) {
        double num = 0.0;

        try {
            return Double.parseDouble(mSharedPreferences.getString(key, ""));
        } catch (Exception e) {}

        return num;
    }

    public <T> T getPrefObject(String key, Class<T> a) {
        String gson = mSharedPreferences.getString(key, null);
        if (gson == null) {
            return null;
        }
        else {
            try {
                return GSON.fromJson(gson, a);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Object stored with key "
                        + key + " is instance of other class");
            }
        }
    }

    public void saveKey(String key, String value) {
        mEditor.putString(key, value).commit();
    }

    public void saveKey(String key, int value) {
        mEditor.putString(key, Integer.toString(value)).commit();
    }

    public void saveKey(String key, Double value) {
        mEditor.putString(key, Double.toString(value)).commit();
    }

    public void saveKey(String key, boolean value) {
        mEditor.putBoolean(key, value).commit();
    }

    public void saveObject(String key, Object object) {
        if (object == null) {
            throw new IllegalArgumentException("Object is null");
        }
        if (key.equals("") || key == null) {
            throw new IllegalArgumentException("Key is empty or null");
        }
        mEditor.putString(key, GSON.toJson(object)).commit();
    }

    public String getFBProfilePic() {
        return getPrefString("FBPROFILEPIC");
    }

    public void resetFBProfilePic() {
        saveKey("FBPROFILEPIC", "");
    }

    public void setFBProfilePic(String url) {
        saveKey("FBPROFILEPIC", url);
    }

    public String getSessionString() {
        return getPrefString(Constants.SESSION_STRING);
    }

    public void setSessionString(String sessionString) {
        saveKey(Constants.SESSION_STRING, sessionString);
    }

    public int getSelectedLanguage() {
        int lang = getPrefInt(SEL_LANGUAGE);

        return lang;
    }

    public void setSelectedLanguage(int language) {
        saveKey(SEL_LANGUAGE, language);
    }

    public void setLoggedInUser(Object object) {
        mEditor.putString(Constants.LOGGED_IN_USER, GSON.toJson(object)).commit();
    }

    public User getLoggedInUser() {
        return getPrefObject(Constants.LOGGED_IN_USER, User.class);
    }

    public void setSelectedMoreHotSearchFilters(int category, List<String> list){
        mEditor.putString(Constants.HOTSEARCHMORE_CATEGORY_FILTERS+String.valueOf(category), android.text.TextUtils.join(",", list)).commit();
    }

    public List<String> getSelectedMoreHotSearchFilters(int category){
        String sel = (getPrefString(Constants.HOTSEARCHMORE_CATEGORY_FILTERS+String.valueOf(category))).trim();

        Log.d("HOT SEARCH MORE", Constants.HOTSEARCHMORE_CATEGORY_FILTERS+String.valueOf(category)+": "+sel);

        if(sel.isEmpty())
            return new ArrayList<String>();

        return new LinkedList<String>(Arrays.asList(sel.split("\\s*,\\s*")));//Arrays.asList(sel.split("\\s*,\\s*"));

    }

    /**
     * Post new snap set selected photos
     * @param list
     */
    public void setSelectedPostPhotos(List<String> list) {
        mEditor.putString(Constants.SELECTED_POST_PHOTOS, android.text.TextUtils.join(",", list)).commit();
    }

    /**
     * Post new snap get selected photos
     * @return
     */
    public List<String> getSelectedPostPhotos() {
        String sel = (getPrefString(Constants.SELECTED_POST_PHOTOS)).trim();

        Log.d("selected photos", Constants.SELECTED_POST_PHOTOS+": "+sel);

        if(sel.isEmpty())
            return new ArrayList<String>();

        return new LinkedList<String>(Arrays.asList(sel.split("\\s*,\\s*")));//Arrays.asList(sel.split("\\s*,\\s*"));
    }

    public void setSelectedRestaurant(Object object){
        mEditor.putString(Constants.SELECTEDRESTAURANT, GSON.toJson(object)).commit();
    }

    public Restaurant getSelectedRestaurant(){
        return getPrefObject(Constants.SELECTEDRESTAURANT, Restaurant.class);
    }

    public boolean getIsFacebookUser() {
        return getPrefBoolean(Constants.ISFBUSER);
    }

    public void setIsFacebookUser(boolean flag) {
        mEditor.putBoolean(Constants.ISFBUSER, flag).commit();
    }

    public int getFileUploadSettingSize() {
        return getPrefInt("fileuploadsize");
    }

    public List<String> getFileUploadSettingTypes() {
        String sel = (getPrefString("mimetypes")).trim();

        if(sel.isEmpty())
            return new ArrayList<String>();

        return new LinkedList<String>(Arrays.asList(sel.split("\\s*,\\s*")));//Arrays.asList(sel.split("\\s*,\\s*"));
    }

    public void setFileUploadSettings(ResponseFileUploadSettings res) {
        if(res.getAllowedMime() == null) {
            saveKey("mimetypes", "");
            saveKey("fileuploadsize", 0);
        } else {
            Log.d("mime", android.text.TextUtils.join(",", res.getAllowedMime()));
            Log.d("size", res.getMaxUploadFileSize()+"");
            saveKey("mimetypes", android.text.TextUtils.join(",", res.getAllowedMime()));
            saveKey("fileuploadsize", res.getMaxUploadFileSize());
        }
    }
}
