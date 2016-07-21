package com.accordhk.SnapNEat.models;

import java.util.List;

/**
 * Created by jm on 21/1/16.
 */
public class District {

    public static String LATITUDE = "latitude";
    public static String LONGITUDE = "longitude";

    private int id;
    private String nId;
    private String name;
    private float latitude;
    private float longitude;
    private List<Snap> snaps;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getnId() {
        return nId;
    }

    public void setnId(String nId) {
        this.nId = nId;
        setId(Integer.parseInt(nId));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public List<Snap> getSnaps() {
        return snaps;
    }

    public void setSnaps(List<Snap> snaps) {
        this.snaps = snaps;
    }
}
