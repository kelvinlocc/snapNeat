package com.accordhk.SnapNEat.utils;

/**
 * Created by jm on 22/1/16.
 */
public class Constants {
//    public static final String BASE_URL = "http://snapneatdev.accordhkcloud.com/service/"; //"https://server[:port]/service/";
//    public static final String BASE_URL = "http://snapneatuat.accordhkcloud.com/service/";

//    public static final String BASE_URL = "http://snapneatdev.accordhkcloudapi.com/service/";
    public static final String BASE_URL = "http://snapneatuat.accordhkcloudapi.com/service/";

    public static final String BASE_USER = BASE_URL+"user";
    public static final String BASE_DEVICE = BASE_URL+"device";
    public static final String BASE_HOT_SEARCH = BASE_URL+"hot_search";
    public static final String BASE_SNAPS = BASE_URL+"snaps";
    public static final String BASE_RESTAURANT = BASE_URL+"restaurant";
    public static final String BASE_REASON_INAPPROPRIATE = BASE_URL+"reasons_inappropriate";
    public static final String BASE_FILE_SETTINGS = BASE_URL+"setting";

    public static final String USER_REGISTER = BASE_USER+"/register";
    public static final String USER_LOGIN = BASE_USER+"/login";
    public static final String USER_FBLOGIN = BASE_USER+"/fblogin";
    public static final String USER_LOGOUT = BASE_USER+"/logout";
    public static final String USER_PROFILE = BASE_USER+"/profile";
    public static final String USER_PROFILE_UPDATE = BASE_USER+"/profile_update";
    public static final String USER_CHANGE_PASSWORD = BASE_USER+"/change_password";
    public static final String USER_RESET_PASSWORD = BASE_USER+"/reset_password";
    public static final String USER_FOLLOWINGS = BASE_USER+"/followings";
    public static final String USER_FOLLOWERS = BASE_USER+"/followers";
    public static final String USER_FOOTPRINTS = BASE_USER+"/footprints";
    public static final String USER_GALLERY = BASE_USER+"/gallery";
    public static final String USER_FOLLOW = BASE_USER+"/follow";
    public static final String USER_NOTIFICATION = BASE_USER+"/notification";

    public static final String DEVICE_REGISTER = BASE_DEVICE+"/register";

    public static final String SNAPS_POPULAR = BASE_SNAPS+"/popular";
    public static final String SNAPS_FOODHASH = BASE_SNAPS+"/foodhash";
    public static final String SNAPS_BY_USER = BASE_SNAPS+"/user";
    public static final String SNAPS_FOLLOWINGS = BASE_SNAPS+"/followings";
    public static final String SNAPS_FAVOURITES = BASE_SNAPS+"/favourites";
    public static final String SNAPS_REPORT_INAPPROPRIATE = BASE_SNAPS+"/report_inappropriate";
    public static final String SNAPS_LIKE = BASE_SNAPS+"/like";
    public static final String SNAPS_ADD_FAVE = BASE_SNAPS+"/add_favourites";
    public static final String SNAPS_POST = BASE_SNAPS+"/post";

    public static final String RESTAURANT_LIST = BASE_RESTAURANT+"/list";
    public static final String RESTAURANT_ADD = BASE_RESTAURANT+"/add";



    public static final String DEEPLINK_SNAP_URL = BASE_URL+"deeplink?snapId=";
    public static final String DEEPLINK_RESTAURANT_URL = BASE_URL+"deeplink?restaurantId=";

    public static final int RES_SUCCESS = 100; // Response SUCCESS
    public static final int RES_UNAUTHORIZED = 106; // Response Unauthorized

    public static final int IMAGE_MAX_BITMAP_DIMENSION = 1024;

    public static final String STR_PAGE = "page";
    public static final String STR_SELECTED_LANGUAGE = "selected_language";

    public static final String LOGGED_IN_USER = "loggedInUser";

    public static final int MINIMUM_PASSWORD_LENGTH = 8;

    public static final String SESSION_STRING = "session_string";

    public static final String SEED_VALUE = "sWx6OFK1FiKnrC2RLn3xj7Ys7zSq7tkoDfr6kE1m";

    public static final String DB_NAME = "snapneat_db";
    public static final int DB_VERSION = 1;

    public static final String PARAM_PAGE = "page";

    public static final String HOTSEARCHMORE_CATEGORY_FILTERS = "HOTSEARCHMORE_CATEGORY_FILTERS_";
    public static final String SELECTEDRESTAURANT = "SELECTEDRESTAURANT";
    public static final String ISFBUSER = "ISFBUSER";

    public static final String SELECTED_POST_PHOTOS = "SELECTED_POST_PHOTOS";

    public static final int OS_TYPE = 1; //ANDROID

    public static final float FAKE_LAT_LONG = -10000f;

    public static final int FLAG_FALSE = 0;
    public static final int FLAG_TRUE = 1;;

    public static final String FILE_TYPE_JPG = "jpeg";

    public static final String PHOTO_PATH = "photoPath";
    public static final String PHOTO_BITMAP = "photoBitmap";

    public static final String FACEBOOK_PACKAGE = "com.facebook.katana";
    public static final String FACEBOOK_URL = "https://fb.me/1744132555827153";
    public static final String INSTAGRAM_PACKAGE = "com.instagram.android";
    public static final String WECHAT_PACKAGE = "com.tencent.mm";

    //wechat
    public static final String WECHAT_APPID = "wx08b3188cf01b49d5";//"wxd930ea5d5a258f4f";//"fb1692076544366088";

    public enum GENDER {
        MALE(1, "Male"), FEMALE(2, "Female"), UNSPECIFIED(0, "Unspecified");

        private int key;
        private String value;

        GENDER(int key, String value) {
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

    public enum HOMEPAGE_SNAP_MODE {
        RANDOM(0, "Random"), LOCATION(1, "Location");

        private int key;
        private String value;

        HOMEPAGE_SNAP_MODE(int key, String value) {
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

    public enum LANGUAGE {
        ENG(0, "en"), TRAD(1, "zh_TW"), SIMP(2, "zh_CN");

        private int key;
        private String value;

        LANGUAGE(int key, String value) {
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

    public enum DIALOG_TYPE {
        OK(0, "OK"), CANCEL(1, "Cancel");

        private int key;
        private String value;

        DIALOG_TYPE(int key, String value) {
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
}
