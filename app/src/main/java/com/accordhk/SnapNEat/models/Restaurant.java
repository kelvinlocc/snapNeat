package com.accordhk.SnapNEat.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jm on 21/1/16.
 */
public class Restaurant {

    public static final String RESTAURANT_ID = "id";
    public static final String RESTAURANT_NID = "nId";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String NAME = "name";
    public static final String DISTANCE = "distance";

    private String nId;
    private int id;
    private String name;
    private String location;
    private float latitude;
    private float longitude;
    private String description;
    private int rating;
    private String contact;
    private String hours;
    private String website;
    private List<Image> photos;
    private List<Dish> dishes;
    @SerializedName("spending_info")
    private Spending spending;
    @SerializedName("resto_food")
    private List<Snap> restoFood;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public List<Image> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Image> photos) {
        this.photos = photos;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public Spending getSpending() {
        return spending;
    }

    public void setSpending(Spending spending) {
        this.spending = spending;
    }

    public List<Snap> getRestoFood() {
        return restoFood;
    }

    public void setRestoFood(List<Snap> restoFood) {
        this.restoFood = restoFood;
    }
}
