package com.accordhk.SnapNEat.models;

/**
 * Created by jm on 21/1/16.
 */
public class HotSearch {

    public enum Category {
        DISTRICT(0, "District"), DISH(1, "Dish"), SPENDINGS(2, "Spending"), HASHTAGS(3, "Hash Tags");

        private int key;
        private String value;

        Category(int key, String value) {
            this.key = key;
            this.value = value;
        }

        public int getKey() {
            return key;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
    public enum Type {
        SELECT(0, "Select"), RANGE(1, "Range");

        private int key;
        private String value;

        Type(int key, String value) {
            this.key = key;
            this.value = value;
        }

        public int getKey() {
            return key;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public enum Show {
        SHOW(0, "Show"), HIDE(1, "Hide");

        private int key;
        private String value;

        Show(int key, String value) {
            this.key = key;
            this.value = value;
        }

        public int getKey() {
            return key;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public enum SearchResultType {
        HASH(0, "Hash"), USER(1, "User");

        private int key;
        private String value;

        SearchResultType(int key, String value) {
            this.key = key;
            this.value = value;
        }

        public int getKey() {
            return key;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    private int id;
    private int category;
    private String value;
    private float from;
    private float to;
    private int type;
    private int isShown;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public float getFrom() {
        return from;
    }

    public void setFrom(float from) {
        this.from = from;
    }

    public float getTo() {
        return to;
    }

    public void setTo(float to) {
        this.to = to;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIsShown() {
        return isShown;
    }

    public void setIsShown(int isShown) {
        this.isShown = isShown;
    }
}
