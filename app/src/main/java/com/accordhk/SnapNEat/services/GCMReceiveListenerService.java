package com.accordhk.SnapNEat.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.accordhk.SnapNEat.MainActivity;
import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.models.PushNotificationPayload;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by jm on 3/3/16.
 */
public class GCMReceiveListenerService extends GcmListenerService {
    public static final int NOTIFICATION_ID = 1;

    @Override
    public void onMessageReceived(String from, Bundle data) {
//        super.onMessageReceived(from, data);
        Log.d("You are here", "GCM");
        String string = "Bundle{";
        for (String key : data.keySet()) {
            string += " " + key + " => " + data.get(key) + ";";
        }
        string += " }Bundle";
        Log.d("BUNDLE DATA: ", string);

//        PushNotificationPayload payload = new Gson().fromJson(String.valueOf(data.get("aps")), PushNotificationPayload.class);
//
//        PushNotificationPayload.Acme acme = payload.getAcme();
//        Log.d("ACME", "TYPE: "+acme.getType());
//        PushNotificationPayload.Alert alert = payload.getAlert();

        //TODO: extract message from bundle
        try {
//            sendNotification(alert.getBody().get("body"));
            sendNotification(data.get("message").toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.SHOW_PROFILE_FROM_PUSH, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("SnapNEat")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}
