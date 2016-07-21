package com.accordhk.SnapNEat.models;

import java.util.List;

/**
 * Created by jm on 6/2/16.
 */
public class ResponseSnapsHomepage extends BaseResponse {
    private List<Snap> results;

    public List<Snap> getResults() {
        return results;
    }

    public void setResults(List<Snap> results) {
        this.results = results;
    }
}
