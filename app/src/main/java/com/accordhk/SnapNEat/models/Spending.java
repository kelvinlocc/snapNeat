package com.accordhk.SnapNEat.models;

/**
 * Created by jm on 21/1/16.
 */
public class Spending {
//    private int id;
//    private float from;
//    private float to;
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public float getFrom() {
//        return from;
//    }
//
//    public void setFrom(float from) {
//        this.from = from;
//    }
//
//    public float getTo() {
//        return to;
//    }
//
//    public void setTo(float to) {
//        this.to = to;
//    }

    private int id;
    private String nId;
    private int category;
    private String value;
    private int type;
    private String title;

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

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
