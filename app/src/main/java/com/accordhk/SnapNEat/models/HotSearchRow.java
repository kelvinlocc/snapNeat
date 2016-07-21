package com.accordhk.SnapNEat.models;

import java.util.List;

/**
 * Created by jm on 21/1/16.
 */
public class HotSearchRow {

    private int type;
    private List<HotSearch> list;

    // for header
    private int image;
    private String label;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<HotSearch> getList() {
        return list;
    }

    public void setList(List<HotSearch> list) {
        this.list = list;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    //    private int type;
//    private List<Object> data;
//
//    private int hotSearchType;
//
//    // for header
//    private int image;
//    private String label;
//
//    public enum ROWTYPE {
//        HEADER(0, "Header"), BODY(1, "Body"), BORDER(2, "Border");
//
//        private int key;
//        private String value;
//
//        ROWTYPE(int key, String value) {
//            this.key = key;
//            this.value = value;
//        }
//
//        public int getKey() {
//            return key;
//        }
//
//        public void setKey(int key) {
//            this.key = key;
//        }
//
//        public String getValue() {
//            return value;
//        }
//
//        public void setValue(String value) {
//            this.value = value;
//        }
//    }
//
//    public int getType() {
//        return type;
//    }
//
//    public void setType(int type) {
//        this.type = type;
//    }
//
//    public List<Object> getData() {
//        return data;
//    }
//
//    public void setData(List<Object> data) {
//        this.data = data;
//    }
//
//    public int getImage() {
//        return image;
//    }
//
//    public void setImage(int image) {
//        this.image = image;
//    }
//
//    public String getLabel() {
//        return label;
//    }
//
//    public void setLabel(String label) {
//        this.label = label;
//    }
//
//    public int getHotSearchType() {
//        return hotSearchType;
//    }
//
//    public void setHotSearchType(int hotSearchType) {
//        this.hotSearchType = hotSearchType;
//    }
}
