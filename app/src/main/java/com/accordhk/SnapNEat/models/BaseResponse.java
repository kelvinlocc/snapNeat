package com.accordhk.SnapNEat.models;

/**
 * Created by jm on 21/1/16.
 */
public class BaseResponse {
    private int status;
    private String message;
    private String favourite_flag;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFavourite_flag (){
        return favourite_flag;
    }

    public void setFavourite_flag(String string){
        this.favourite_flag=string;
    }
}
