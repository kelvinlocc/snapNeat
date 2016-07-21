package com.accordhk.SnapNEat.models;

/**
 * Created by jm on 6/2/16.
 */
public class ResponseSnapDetails extends BaseResponse {
    private Snap results;

    public Snap getResults() {
        return results;
    }

    public void setResults(Snap results) {
        this.results = results;
    }
}
