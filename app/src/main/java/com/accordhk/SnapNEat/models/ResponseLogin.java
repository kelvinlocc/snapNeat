package com.accordhk.SnapNEat.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jm on 6/2/16.
 */
public class ResponseLogin extends BaseResponse {
    @SerializedName("user_id")
    private int userId;

    @SerializedName("session_string")
    private String sessionString;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSessionString() {
        return sessionString;
    }

    public void setSessionString(String sessionString) {
        this.sessionString = sessionString;
    }
}
