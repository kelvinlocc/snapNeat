package com.accordhk.SnapNEat.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jm on 6/2/16.
 */
public class ListResponse extends BaseResponse {
    @SerializedName("total_records")
    private int totalRecords;
    @SerializedName("current_page_no")
    private int currentPageNo;
    @SerializedName("records_per_page")
    private int recordsPerPage;

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getCurrentPageNo() {
        return currentPageNo;
    }

    public void setCurrentPageNo(int currentPageNo) {
        this.currentPageNo = currentPageNo;
    }

    public int getRecordsPerPage() {
        return recordsPerPage;
    }

    public void setRecordsPerPage(int recordsPerPage) {
        this.recordsPerPage = recordsPerPage;
    }
}
