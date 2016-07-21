package com.accordhk.SnapNEat.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jm on 15/4/16.
 */
public class ResponseFileUploadSettings extends BaseResponse {
    @SerializedName("max_upload_file_size")
    private int maxUploadFileSize;

    @SerializedName("allowed_mime")
    private List<String> allowedMime;

    public int getMaxUploadFileSize() {
        return maxUploadFileSize;
    }

    public void setMaxUploadFileSize(int maxUploadFileSize) {
        this.maxUploadFileSize = maxUploadFileSize;
    }

    public List<String> getAllowedMime() {
        return allowedMime;
    }

    public void setAllowedMime(List<String> allowedMime) {
        this.allowedMime = allowedMime;
    }
}
