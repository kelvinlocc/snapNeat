package com.accordhk.SnapNEat.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jm on 6/2/16.
 */
public class ResponseHotSearchFilter extends BaseResponse {
    @SerializedName("main_options")
    private List<HotSearch> mainOptions;
    @SerializedName("other_options")
    private List<HotSearch> otherOptions;

    public List<HotSearch> getMainOptions() {
        return mainOptions;
    }

    public void setMainOptions(List<HotSearch> mainOptions) {
        this.mainOptions = mainOptions;
    }

    public List<HotSearch> getOtherOptions() {
        return otherOptions;
    }

    public void setOtherOptions(List<HotSearch> otherOptions) {
        this.otherOptions = otherOptions;
    }
}
