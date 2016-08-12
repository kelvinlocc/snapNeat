package com.accordhk.SnapNEat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.telecom.StatusHints;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by KelvinLo on 8/12/2016.
 */
public class mySharePreference_app {
    static String preference_key = "my_preference";
    static String editor_key = "USER_ID";
    static String like_key = "check_like";
    String TAG = this.getClass().getName();

    public void setUserId (Context context, String user_id){
        Log.i(TAG, "setUserID: user_id "+user_id);
        Toast.makeText(context, "user_id "+user_id, Toast.LENGTH_SHORT).show();

        SharedPreferences sharedPreferences = context.getSharedPreferences(preference_key,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(editor_key,user_id);
        editor.apply();

    }

    public int getUserID (Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(preference_key,Context.MODE_PRIVATE);
        int user_id =  Integer.parseInt(sharedPreferences.getString(editor_key,"not found user id !"));
        Log.i(TAG, "getUserID: user_id "+user_id);
        Toast.makeText(context, "user_id "+user_id, Toast.LENGTH_SHORT).show();
        return user_id;

    }
    public void like (Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(preference_key,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(like_key,true);
        editor.apply();
    }

    public void dislike (Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(preference_key,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(like_key,false);
        editor.apply();
    }

    public boolean isLike (Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(preference_key,Context.MODE_PRIVATE);

        return sharedPreferences.getBoolean(like_key,false);
    }






















}
