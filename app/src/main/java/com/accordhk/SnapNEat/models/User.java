package com.accordhk.SnapNEat.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jm on 21/1/16.
 */
public class User {

    public static String UDID = "udid";
    public static String USER_ID = "user_id";
    public static String EMAIL = "email";
    public static String USERNAME = "username";
    public static String PASSWORD = "password";
    public static String NEW_PASSWORD = "new_password";
    public static String FB_UID = "fb_uid";
    public static String FB_TOKEN = "fb_token";
    public static String FB_AVATAR_URL = "fb_avatar_url";
    public static String FB_USERNAME = "fb_username";
    public static String AVATAR = "avatar";
    public static String FILE_TYPE = "file_type";
    public static String GENDER = "gender";
    public static String PHONE = "phone";
    public static String ABOUT = "about";
    public static String OS_TYPE = "os_type";
    public static String ALLOW_NOTIF = "allow_notification";

    public enum Gender {
        MALE(0, "Male"), Female(1, "Female"), Unspecified(2, "Unspecified");

        private int key;
        private String value;

        Gender(int key, String value) {
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

    @SerializedName("user_id")
    private int userId;

    private int id;
    private int nId;
    private String email;
    private String username;
    private String password;
    @SerializedName("new_password")
    private String newPassword;
    @SerializedName("fb_uid")
    private String fbUid;
    @SerializedName("fb_token")
    private String fbToken;
    private String avatar;
    @SerializedName("avatar_thumbnail")
    private String avatarThumbnail;
    @SerializedName("os_type")
    private int osType;
    private String udid;
    private int gender;
    private String phone;
    @SerializedName("hash_tags")
    private String hashTags;
    private String about;
    private List<Snap> snaps;

    @SerializedName("follow_flag")
    private int followFlag;

    @SerializedName("notif_flag")
    private int notificationFlag;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getFbUid() {
        return fbUid;
    }

    public void setFbUid(String fbUid) {
        this.fbUid = fbUid;
    }

    public String getFbToken() {
        return fbToken;
    }

    public void setFbToken(String fbToken) {
        this.fbToken = fbToken;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatarThumbnail() {
        return avatarThumbnail;
    }

    public void setAvatarThumbnail(String avatarThumbnail) {
        this.avatarThumbnail = avatarThumbnail;
    }

    public int getOsType() {
        return osType;
    }

    public void setOsType(int osType) {
        this.osType = osType;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHashTags() {
        return hashTags;
    }

    public void setHashTags(String hashTags) {
        this.hashTags = hashTags;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public List<Snap> getSnaps() {
        return snaps;
    }

    public void setSnaps(List<Snap> snaps) {
        this.snaps = snaps;
    }

    public int getFollowFlag() {
        return followFlag;
    }

    public void setFollowFlag(int followFlag) {
        this.followFlag = followFlag;
    }

    public int getNotificationFlag() {
        return notificationFlag;
    }

    public void setNotificationFlag(int notificationFlag) {
        this.notificationFlag = notificationFlag;
    }
}
