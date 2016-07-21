package com.accordhk.SnapNEat.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jm on 21/1/16.
 */
public class Snap {

    public static final String SNAP_ID = "snap_id";
    public static final String USER_ID = "user_id";
    public static final String MODE = "mode";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String DISTANCE = "distance";

    public static final String RATE = "rate";
    public static final String TITLE = "title";
    public static final String RESTAURANT_ID = "restaurant_id";
    public static final String SPENDING_ID = "spending_id";
    public static final String DISH = "dish[]";
    public static final String DISH_ID = "dish_id";
    public static final String COMMENTS = "comments";
    public static final String HASHTAGS = "hashtags";
    public static final String PHOTOS = "photos[]";
    public static final String FILE_TYPES = "file_types[]";
    public static final String FILE_NAMES = "file_names";
    public static final String RATING = "rating";

    private int mode;
    private float latitude;
    private float longitude;
    @SerializedName("snap_id")
    private int snapId;

    private String nId;

    private int id;
    private String title;
    private String image;
    @SerializedName("image_thumbnail")
    private String imageThumbnail;

    private int rating;
    @SerializedName("total_likes")
    private int totalLikes;
    @SerializedName("district_info")
    private District district;
    @SerializedName("user_info")
    private User user;

    private String commens;
    private String comments;
    @SerializedName("snap_photos")
    private List<String> photos;
    @SerializedName("spending_info")
    private Spending spending;
    @SerializedName("dish_info")
    private List<Dish> dishes;
    @SerializedName("restaurant_info")
    private Restaurant restaurant;
    @SerializedName("related_snaps")
    private List<Snap> relatedSnaps;

    @SerializedName("like_flag")
    private int likeFlag;
    @SerializedName("favourite_flag")
    private int favouriteFlag;
    @SerializedName("inappropriate_flag")
    private int inappropriateFlag;

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
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

    public int getSnapId() {
        return snapId;
    }

    public void setSnapId(int snapId) {
        this.snapId = snapId;
    }

    public String getnId() {
        return nId;
    }

    public void setnId(String nId) {
        this.nId = nId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(int totalLikes) {
        this.totalLikes = totalLikes;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCommens() {
        return commens;
    }

    public void setCommens(String commens) {
        this.commens = commens;
        setComments(commens);
    }

    public String getComments() {
        if(comments == null) {
            if(this.commens.isEmpty() == false)
                this.comments = this.commens;
            else
                this.comments = "";
        }
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public Spending getSpending() {
        return spending;
    }

    public void setSpending(Spending spending) {
        this.spending = spending;
    }

//    public Dish getDish() {
//        return dish;
//    }
//
//    public void setDish(Dish dish) {
//        this.dish = dish;
//    }


    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public List<Snap> getRelatedSnaps() {
        return relatedSnaps;
    }

    public void setRelatedSnaps(List<Snap> relatedSnaps) {
        this.relatedSnaps = relatedSnaps;
    }

    public int getLikeFlag() {
        return likeFlag;
    }

    public void setLikeFlag(int likeFlag) {
        this.likeFlag = likeFlag;
    }

    public int getFavouriteFlag() {
        return favouriteFlag;
    }

    public void setFavouriteFlag(int favouriteFlag) {
        this.favouriteFlag = favouriteFlag;
    }

    public int getInappropriateFlag() {
        return inappropriateFlag;
    }

    public void setInappropriateFlag(int inappropriateFlag) {
        this.inappropriateFlag = inappropriateFlag;
    }
}
