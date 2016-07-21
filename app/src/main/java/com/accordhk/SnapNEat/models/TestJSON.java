package com.accordhk.SnapNEat.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jm on 7/2/16.
 */
public class TestJSON {
    private String time;
    @SerializedName("milliseconds_since_epoch")
    private String millisecondsSinceEpoch;
    private String date;

    public String getMillisecondsSinceEpoch() {
        return millisecondsSinceEpoch;
    }

    public void setMillisecondsSinceEpoch(String millisecondsSinceEpoch) {
        this.millisecondsSinceEpoch = millisecondsSinceEpoch;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
