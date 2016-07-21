package com.accordhk.SnapNEat.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jm on 21/1/16.
 */
public class Image {
    private int id;
    private int nId;
    private String image;
    @SerializedName("image_thumbnail")
    private String imageThumbnail;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getnId() {
        return nId;
    }

    public void setnId(int nId) {
        this.nId = nId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageThumbnail() {
        return imageThumbnail;
    }

    public void setImageThumbnail(String imageThumbnail) {
        this.imageThumbnail = imageThumbnail;
    }
}
