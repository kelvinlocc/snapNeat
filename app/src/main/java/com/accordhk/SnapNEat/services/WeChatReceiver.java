package com.accordhk.SnapNEat.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.accordhk.SnapNEat.utils.Constants;
import com.google.android.gms.iid.InstanceIDListenerService;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by jm on 3/3/16.
 */
public class WeChatReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final IWXAPI api = WXAPIFactory.createWXAPI(context, null);
        api.registerApp(Constants.WECHAT_APPID);
    }
}
