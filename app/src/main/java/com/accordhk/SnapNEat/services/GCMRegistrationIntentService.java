package com.accordhk.SnapNEat.services;

import android.app.IntentService;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.models.BaseResponse;
import com.accordhk.SnapNEat.utils.Constants;
import com.accordhk.SnapNEat.utils.SharedPref;
import com.accordhk.SnapNEat.utils.Utils;
import com.android.volley.VolleyError;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.util.Map;

/**
 * Created by jm on 3/3/16.
 */
public class GCMRegistrationIntentService extends IntentService {

    private static final String TAG = "GCMRegIntentService";
    private static final String[] TOPICS = {"global"};

    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";

    public GCMRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPref sharedPreferences = new SharedPref(getApplicationContext());
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            boolean sentToken = sharedPreferences.getPrefBoolean(GCMRegistrationIntentService.SENT_TOKEN_TO_SERVER);

            if(sentToken == false) {
                // [START register_for_gcm]
                // Initially this call goes out to the network to retrieve the token, subsequent calls
                // are local.
                // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
                // See https://developers.google.com/cloud-messaging/android/start for details on this file.
                // [START get_token]

                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                // [END get_token]
                Log.i(TAG, "GCM Registration Token: " + token);
                sendRegistrationToServer(token);
            }


            // Subscribe to topic channels
//            subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
//            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
//            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
            sharedPreferences.saveKey(SENT_TOKEN_TO_SERVER, false);
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        try {
            final Utils mUtils = new Utils(getApplicationContext());

            String udid = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

            Map<String, String> params = mUtils.getBaseRequestMap();
            params.put("os_type", String.valueOf(Constants.OS_TYPE));
            params.put("udid", udid);
            params.put("token", token);

            (new ApiWebServices()).getInstance(getApplicationContext()).registerDevice(params, new ApiWebServices.ApiListener() {
                @Override
                public void onResponse(Object object) {
                    try {
                        BaseResponse response = (BaseResponse) object;
                        if (response != null) {
                            if (response.getStatus() == Constants.RES_SUCCESS)
                                new SharedPref(getApplicationContext()).saveKey(SENT_TOKEN_TO_SERVER, true);
                            else
                                new SharedPref(getApplicationContext()).saveKey(SENT_TOKEN_TO_SERVER, false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_cannot_process_request)).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]
}
