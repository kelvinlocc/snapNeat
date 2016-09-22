package com.accordhk.SnapNEat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by KelvinLo on 8/12/2016.
 */
public class mySharePreference_app {
    static String preference_key = "my_preference";
    static String editor_key = "USER_ID";
    static String like_key = "like_key";
    static String snap_id_key = "snap_id_key";
    String TAG = this.getClass().getName();
    static String update_like_key = "update_like_key";

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

    // these 3 method is for update like in homepage
//    public void setSnap

    public void updateUserLike (Context context,int snap_id,boolean bool){
        SharedPreferences sharedPreferences = context.getSharedPreferences(update_like_key,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(snap_id_key,snap_id);
        editor.putBoolean(like_key,bool);
        editor.apply();
    }

    public int get_snap_id (Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(update_like_key,Context.MODE_PRIVATE);
        int snap_id =  sharedPreferences.getInt(snap_id_key,0);
        Log.i(TAG, "getUserID: snap_id "+snap_id);
//        Toast.makeText(context, "snap_id "+snap_id, Toast.LENGTH_SHORT).show();
        return snap_id;
    }
    public boolean get_like (Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(update_like_key,Context.MODE_PRIVATE);
        boolean like =  sharedPreferences.getBoolean(like_key,false);
        Log.i(TAG, "getUserID: like "+like);
//        Toast.makeText(context, "like "+like, Toast.LENGTH_SHORT).show();
        return like;
    }





    public void setLike (Context context,int snap_id,boolean bool){
       setLike(context,Integer.toString(snap_id),bool);
    }

    public void setLike (Context context,String snap_id,boolean bool){
        SharedPreferences sharedPreferences = context.getSharedPreferences(snap_id,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(like_key,bool);
        editor.apply();
    }
    public void getLike (Context context,int snap_id){
     getLike(context,Integer.toString(snap_id));

    }

    public void getLike (Context context,String snap_id){
        SharedPreferences sharedPreferences = context.getSharedPreferences(snap_id,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(like_key,false);
        editor.apply();
    }

    public boolean isLike (Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(preference_key,Context.MODE_PRIVATE);

        return sharedPreferences.getBoolean(like_key,false);
    }






















}
