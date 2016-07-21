package com.accordhk.SnapNEat.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jm on 6/2/16.
 */
public class ResponsePostLike extends BaseResponse {
    @SerializedName("total_likes")
    private int totalLikes;
    @SerializedName("like_status")
    private int likeStatus;

    public int getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(int totalLikes) {
        this.totalLikes = totalLikes;
    }

    public int getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(int likeStatus) {
        this.likeStatus = likeStatus;
    }
}
