package com.accordhk.SnapNEat.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jm on 6/2/16.
 */
public class ResponseUserProfile extends BaseResponse {
    @SerializedName("total_followings")
    private int totalFollowings;
    @SerializedName("total_followers")
    private int totalFollowers;
    @SerializedName("total_snaps")
    private int totalSnaps;
    @SerializedName("user_info")
    private User userInfo;

    public int getTotalFollowings() {
        return totalFollowings;
    }

    public void setTotalFollowings(int totalFollowings) {
        this.totalFollowings = totalFollowings;
    }

    public int getTotalFollowers() {
        return totalFollowers;
    }

    public void setTotalFollowers(int totalFollowers) {
        this.totalFollowers = totalFollowers;
    }

    public int getTotalSnaps() {
        return totalSnaps;
    }

    public void setTotalSnaps(int totalSnaps) {
        this.totalSnaps = totalSnaps;
    }

    public User getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(User userInfo) {
        this.userInfo = userInfo;
    }
}
