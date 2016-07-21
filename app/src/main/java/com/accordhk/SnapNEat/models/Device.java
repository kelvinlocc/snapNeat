package com.accordhk.SnapNEat.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jm on 21/1/16.
 */
public class Device {
    @SerializedName("device_id")
    private int id;
    private String udid;
    private String token;
    @SerializedName("os_type")
    private int osType;
    @SerializedName("allow_notification")
    private int allowNotification;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getOsType() {
        return osType;
    }

    public void setOsType(int osType) {
        this.osType = osType;
    }

    public int getAllowNotification() {
        return allowNotification;
    }

    public void setAllowNotification(int allowNotification) {
        this.allowNotification = allowNotification;
    }
}
