package com.accordhk.SnapNEat.models;

import java.util.List;

/**
 * Created by jm on 6/2/16.
 */
public class ResponseListUsers extends ListResponse {
    private List<User> results;

    public List<User> getResults() {
        return results;
    }

    public void setResults(List<User> results) {
        this.results = results;
    }
}
