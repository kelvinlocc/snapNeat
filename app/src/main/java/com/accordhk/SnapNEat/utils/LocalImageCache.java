package com.accordhk.SnapNEat.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by jm on 27/2/16.
 */
public class LocalImageCache implements ImageLoader.ImageCache{

    @Override
    public Bitmap getBitmap(String url) {
        // Here you can add an actual cache
//        if (url.contains("file://")) {
//            return BitmapFactory.decodeFile(url);
//        } else return null;

        return BitmapFactory.decodeFile(url.substring(url.indexOf("/")));
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {

    }
}
