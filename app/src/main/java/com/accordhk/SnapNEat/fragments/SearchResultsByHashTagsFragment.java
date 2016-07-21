package com.accordhk.SnapNEat.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.adapters.SnapListAdapter;
import com.accordhk.SnapNEat.models.HotSearch;
import com.accordhk.SnapNEat.models.ResponseListSnaps;
import com.accordhk.SnapNEat.models.Snap;
import com.accordhk.SnapNEat.models.User;
import com.accordhk.SnapNEat.services.ApiWebServices;
import com.accordhk.SnapNEat.utils.Constants;
import com.accordhk.SnapNEat.utils.CustomFontEditText;
import com.accordhk.SnapNEat.utils.CustomFontTextView;
import com.accordhk.SnapNEat.utils.SharedPref;
import com.accordhk.SnapNEat.utils.VolleySingleton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchResultsByHashTagsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchResultsByHashTagsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResultsByHashTagsFragment extends BaseSearchFragment {
    private static String LOGGER_TAG = "SearchResultsByHashTagsFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String SEARCH_CATEGORIES = "searchCategories";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private String filters;

    private HashMap<Integer, List<String>> selectedValuesMap;

    public SearchResultsByHashTagsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchResultsByHashTagsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchResultsByHashTagsFragment newInstance(String param1, String param2) {
        SearchResultsByHashTagsFragment fragment = new SearchResultsByHashTagsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectedValuesMap = new HashMap<Integer, List<String>>();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

            searchString = getArguments().getString(SEARCH_STRING);
            searchType = getArguments().getInt(SEARCH_TYPE);
            if(getArguments().getSerializable(SEARCH_CATEGORIES) != null)
                selectedValuesMap = (HashMap<Integer, List<String>>) getArguments().getSerializable(SEARCH_CATEGORIES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_search_results_list, container, false);
        final CustomFontTextView message = (CustomFontTextView) view.findViewById(R.id.message);
        final GridView gv_search_results = (GridView) view.findViewById(R.id.gv_search_results);

        try {

            Map<String, String> params = mUtils.getBaseRequestMap();
            params.put(Constants.STR_PAGE, String.valueOf(page));
            params.put(SEARCH_STRING, String.valueOf(searchString));

//            final SharedPref sharedPref = new SharedPref(getContext());
//            List<String> cat = sharedPref.getSelectedMoreHotSearchFilters(HotSearch.Category.DISTRICT.getKey());
//            if(cat.isEmpty() == false)
//                params.put(SEARCH_DISTRICT_CATEGORY_FILTERS, android.text.TextUtils.join(",", cat));
//
//            cat = sharedPref.getSelectedMoreHotSearchFilters(HotSearch.Category.DISH.getKey());
//            if(cat.isEmpty() == false)
//                params.put(SEARCH_DISH_CATEGORY_FILTERS, android.text.TextUtils.join(",", cat));
//
//            cat = sharedPref.getSelectedMoreHotSearchFilters(HotSearch.Category.SPENDINGS.getKey());
//            if(cat.isEmpty() == false)
//                params.put(SEARCH_SPENDING_CATEGORY_FILTERS, android.text.TextUtils.join(",", cat));
//
//            cat = sharedPref.getSelectedMoreHotSearchFilters(HotSearch.Category.HASHTAGS.getKey());
//            if(cat.isEmpty() == false)
//                params.put(SEARCH_CATEGORY_FILTERS, android.text.TextUtils.join(",", cat));

            if(selectedValuesMap.containsKey(HotSearch.Category.DISTRICT.getKey())) {
                if(selectedValuesMap.get(HotSearch.Category.DISTRICT.getKey()).isEmpty() == false)
                    params.put(SEARCH_DISTRICT_CATEGORY_FILTERS, android.text.TextUtils.join(",", selectedValuesMap.get(HotSearch.Category.DISTRICT.getKey())));
            }

            if(selectedValuesMap.containsKey(HotSearch.Category.DISH.getKey())) {
                if(selectedValuesMap.get(HotSearch.Category.DISH.getKey()).isEmpty() == false)
                    params.put(SEARCH_DISH_CATEGORY_FILTERS, android.text.TextUtils.join(",", selectedValuesMap.get(HotSearch.Category.DISH.getKey())));
            }

            if(selectedValuesMap.containsKey(HotSearch.Category.SPENDINGS.getKey())) {
                if(selectedValuesMap.get(HotSearch.Category.SPENDINGS.getKey()).isEmpty() == false)
                    params.put(SEARCH_SPENDING_CATEGORY_FILTERS, android.text.TextUtils.join(",", selectedValuesMap.get(HotSearch.Category.SPENDINGS.getKey())));
            }

            if(selectedValuesMap.containsKey(HotSearch.Category.HASHTAGS.getKey())) {
                if(selectedValuesMap.get(HotSearch.Category.HASHTAGS.getKey()).isEmpty() == false)
                    params.put(SEARCH_CATEGORY_FILTERS, android.text.TextUtils.join(",", selectedValuesMap.get(HotSearch.Category.HASHTAGS.getKey())));
            }

            page = 1;

            mProgressDialog.show();
            mApi.getSnapsByFoodHash(params, new ApiWebServices.ApiListener() {
                @Override
                public void onResponse(Object object) {
                    try {
                        Log.d(LOGGER_TAG, "getSnapsByFoodHash");
                        ResponseListSnaps listSnaps = (ResponseListSnaps) object;

                        if (listSnaps != null) {
                            if (listSnaps.getStatus() != Constants.RES_SUCCESS) {
                                mUtils.dismissDialog(mProgressDialog);
                                message.setVisibility(View.VISIBLE);
                                message.setText(listSnaps.getMessage());
                                gv_search_results.setVisibility(View.INVISIBLE);
                            } else {

                                mUtils.dismissDialog(mProgressDialog);
                                if (listSnaps.getResults().size() > 0) {
                                    page = listSnaps.getCurrentPageNo();

                                    message.setVisibility(View.INVISIBLE);
                                    gv_search_results.setVisibility(View.VISIBLE);

                                    final SnapListAdapter adapter = new SnapListAdapter(getContext(), R.layout.list_search_result_by_hash, listSnaps.getResults());

                                    gv_search_results.setAdapter(adapter);
                                    gv_search_results.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            if (mListener != null) {
                                                Snap snap = (Snap) parent.getAdapter().getItem(position);
                                                mListener.showSnapDetails(snap.getId());
                                            }
                                        }
                                    });

                                    gv_search_results.setOnScrollListener(new AbsListView.OnScrollListener() {

                                        private int currentVisibleItemCount;
                                        private int currentScrollState;
                                        private int currentFirstVisibleItem;
                                        private int totalItem;

                                        @Override
                                        public void onScrollStateChanged(AbsListView view, int scrollState) {
                                            this.currentScrollState = scrollState;
                                            if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                                                    && this.currentScrollState == SCROLL_STATE_IDLE) {

                                                page++;
                                                try {
                                                    Map<String, String> params = mUtils.getBaseRequestMap();
                                                    params.put(Constants.STR_PAGE, String.valueOf(page));
                                                    params.put(SEARCH_STRING, String.valueOf(searchString));
                                                    params.put(SEARCH_CATEGORY_FILTERS, filters);

                                                    mProgressDialog.show();
                                                    mApi.getSnapsByFoodHash(params, new ApiWebServices.ApiListener() {
                                                        @Override
                                                        public void onResponse(Object object) {
                                                            try {
                                                                ResponseListSnaps listSnaps = (ResponseListSnaps) object;
                                                                page = listSnaps.getCurrentPageNo();

                                                                if (listSnaps.getResults().size() > 0) {
                                                                    adapter.addAll(listSnaps.getResults());
                                                                    adapter.notifyDataSetChanged();
                                                                }
                                                                mUtils.dismissDialog(mProgressDialog);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                                mUtils.dismissDialog(mProgressDialog);
                                                            }
                                                        }

                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            mUtils.dismissDialog(mProgressDialog);
                                                            mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_cannot_process_request)).show();
                                                        }
                                                    });
                                                } catch (Exception e) {
                                                    mUtils.dismissDialog(mProgressDialog);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                                            this.currentFirstVisibleItem = firstVisibleItem;
                                            this.currentVisibleItemCount = visibleItemCount;
                                            this.totalItem = totalItemCount;
                                        }
                                    });

                                } else {
                                    mUtils.dismissDialog(mProgressDialog);
                                    message.setVisibility(View.VISIBLE);
                                    message.setText(listSnaps.getMessage());
                                    gv_search_results.setVisibility(View.INVISIBLE);
                                }

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUtils.dismissDialog(mProgressDialog);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    mUtils.dismissDialog(mProgressDialog);
                    mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_cannot_process_request)).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            mUtils.dismissDialog(mProgressDialog);
        }

        TextView tv_hot_search_string = (TextView) view.findViewById(R.id.tv_hot_search_string);
        tv_hot_search_string.setText(searchString);
        tv_hot_search_string.setFocusable(false);

        ImageButton btn_back = (ImageButton) view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.goBack();
                }
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        VolleySingleton.getInstance(getContext()).getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void showSnapDetails(int id);
        void goBack();
    }
}
