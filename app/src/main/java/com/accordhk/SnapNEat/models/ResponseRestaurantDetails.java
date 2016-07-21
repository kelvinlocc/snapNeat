package com.accordhk.SnapNEat.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jm on 6/2/16.
 */
public class ResponseRestaurantDetails extends BaseResponse{
    @SerializedName("restaurant_info")
    private Restaurant restaurantInfo;

    public Restaurant getRestaurantInfo() {
        return restaurantInfo;
    }

    public void setRestaurantInfo(Restaurant restaurantInfo) {
        this.restaurantInfo = restaurantInfo;
    }
}
