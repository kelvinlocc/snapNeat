package com.accordhk.SnapNEat.models;

import java.util.List;

/**
 * Created by jm on 6/2/16.
 */
public class ResponseReasonInappropriate extends BaseResponse {
    private List<ReasonInappropriate> results;

    public List<ReasonInappropriate> getResults() {
        return results;
    }

    public void setResults(List<ReasonInappropriate> results) {
        this.results = results;
    }
}
