package com.accordhk.SnapNEat.models;

import java.util.List;

/**
 * Created by jm on 6/2/16.
 */
public class ResponseRestaurants extends ListResponse {
    private List<Restaurant> results;

    public List<Restaurant> getResults() {
        return results;
    }

    public void setResults(List<Restaurant> results) {
        this.results = results;
    }
}
