package com.accordhk.SnapNEat.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by jm on 9/2/16.
 */
public class BaseSearchFragment extends BaseFragment{

    public static String SEARCH_STRING = "search_string";
    public static String SEARCH_TYPE = "search_type";
    public static String SEARCH_CATEGORY_FILTERS = "category_filters";
    public static String SEARCH_DISH_CATEGORY_FILTERS = "dish_category_filters";
    public static String SEARCH_SPENDING_CATEGORY_FILTERS = "spending_category_filters";
    public static String SEARCH_DISTRICT_CATEGORY_FILTERS = "district_category_filters";

    public String searchString;
    public int searchType;

    public int page;
    public int totalRecords;
    public int recordsPerPage;
    public int currentNoOfRecords;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }
}
