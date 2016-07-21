package com.accordhk.SnapNEat.models;

import java.util.List;

/**
 * Created by jm on 6/2/16.
 */
public class ResponseFootprints extends ListResponse {
    private List<District> results;

    public List<District> getResults() {
        return results;
    }

    public void setResults(List<District> results) {
        this.results = results;
    }
}
