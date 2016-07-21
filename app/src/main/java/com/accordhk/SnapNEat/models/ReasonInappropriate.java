package com.accordhk.SnapNEat.models;

/**
 * Created by jm on 6/2/16.
 */
public class ReasonInappropriate {

    public static final String SNAP_ID = "snap_id";
    public static final String REASON_ID = "reason_id";
    public static final String DESCRIPTION = "description";

    private int id;
    private int nId;
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
