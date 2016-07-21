package com.accordhk.SnapNEat.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jm on 6/2/16.
 */
public class ResponseFollowUser extends BaseResponse {
    @SerializedName("total_followers")
    private int totalFollowers;
    @SerializedName("follow_flag")
    private int followFlag;

    public int getTotalFollowers() {
        return totalFollowers;
    }

    public void setTotalFollowers(int totalFollowers) {
        this.totalFollowers = totalFollowers;
    }

    public int getFollowFlag() {
        return followFlag;
    }

    public void setFollowFlag(int followFlag) {
        this.followFlag = followFlag;
    }
}
