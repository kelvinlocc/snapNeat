package com.accordhk.SnapNEat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.accordhk.SnapNEat.models.HotSearch;
import com.accordhk.SnapNEat.models.Restaurant;
import com.accordhk.SnapNEat.services.ApiWebServices;
import com.accordhk.SnapNEat.utils.SharedPref;
import com.accordhk.SnapNEat.utils.Utils;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    private static String LOGGER_TAG = "SplashActivity";
    private Utils mUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mUtils = new Utils(this);

        // get app pref language
        int selectedLang = new SharedPref(this).getSelectedLanguage();

        Locale locale;
        if(selectedLang == -1) {
            locale = getResources().getConfiguration().locale;
        } else {
            locale = mUtils.getLocale(selectedLang);
        }

        Log.d("LANG: ", locale.getLanguage()+"- CountryCode: "+locale.getCountry());

        if(locale.getLanguage().equals("zh") && locale.getCountry().equalsIgnoreCase("cn")) {
            locale = Locale.SIMPLIFIED_CHINESE;
        } else if (locale.getLanguage().equals("zh") && locale.getCountry().equalsIgnoreCase("tw")) {
            locale = locale.TRADITIONAL_CHINESE;
        } else {
            locale = locale.ENGLISH;
        }

        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;

        SharedPref sharedPref = new SharedPref(this);
        sharedPref.setSelectedLanguage(mUtils.getLocale(locale));

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        //reset search options
        mUtils.resetSearchSettings();

        //TODO: check internet connection

//        final ProgressDialog hDialog = new ProgressDialog(this);
//        hDialog.setMessage("Loading");
//        hDialog.show();
//        try {
//            ApiWebServices mApi = new ApiWebServices().getInstance(this);
//
//            Map<String, String> params = mUtils.getBaseRequestMap();
//
//            mApi.getHotSearchFilters(params, new ApiWebServices.ApiListener() {
//                @Override
//                public void onResponse(Object obj) {
//                    Log.d(LOGGER_TAG, "getHotSearchFilters success");
//                    Intent intent = new Intent(SplashActivity.this,MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    error.printStackTrace();
//                    Intent intent = new Intent(SplashActivity.this,MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//            }, false);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Intent intent = new Intent(SplashActivity.this,MainActivity.class);
//            startActivity(intent);
//            finish();
//        }

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        timerThread.start();
    }

}
