package com.accordhk.SnapNEat.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.adapters.HotSearchMoreRowAdapter;
import com.accordhk.SnapNEat.dao.HotSearchFilterDataSource;
import com.accordhk.SnapNEat.models.HotSearch;
import com.accordhk.SnapNEat.utils.CustomFontEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HotSearchMoreListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HotSearchMoreListFragment} factory method to
 * create an instance of this fragment.
 */
public class HotSearchMoreListFragment extends BaseFragment {
    private static String LOGGER_TAG = "HotSearchMoreListFragment";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String DEFAULT_SELECTED_VALUES = "defaultSelectedValues";
    public static final String SEARCH_MORE_TYPE = "searchMoreType";
    public static final String SEARCH_IS_SINGLE_SELECT = "searchIsSingleSelect";
    public static final String SEARCH_IS_SELECTED_VALUE_VALUE = "searchIsSelectedValueName"; // true:if the selected value to be used is the VALUE; false: use ID
    public static final String SHOW_ALL_OPTION = "showAllOption";

    public int searchMoreType;
    private boolean isSingleSelect;
    private boolean isSelectedValueValue;
    private boolean showAllOption;

    public List<String> categoryFilters;

    private OnFragmentInteractionListener mListener;

    public HotSearchMoreListFragment() {
        // Required empty public constructor
    }

    private boolean isAllSelected = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchMoreType = getArguments().getInt(SEARCH_MORE_TYPE);
            isSingleSelect = getArguments().getBoolean(SEARCH_IS_SINGLE_SELECT);
            isSelectedValueValue = getArguments().getBoolean(SEARCH_IS_SELECTED_VALUE_VALUE);
            showAllOption = getArguments().getBoolean(SHOW_ALL_OPTION);
            categoryFilters = mUtils.stringToList(getArguments().getString(DEFAULT_SELECTED_VALUES));

            if(categoryFilters.contains("-1"))
                isAllSelected = true;
        }

//        categoryFilters = new SharedPref(getContext()).getSelectedMoreHotSearchFilters(searchMoreType);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_hot_search_more_list, container, false);

        final CustomFontEditText tv_hot_search_string = (CustomFontEditText) view.findViewById(R.id.tv_hot_search_string);

        AsyncTask<Void, Void, List<HotSearch>> task = new AsyncTask<Void, Void, List<HotSearch>>() {
            @Override
            protected List<HotSearch> doInBackground(Void... params) {
                List<HotSearch> result = new ArrayList<HotSearch>();

                HotSearch allHotSearch = new HotSearch();

                if(isSingleSelect == false && showAllOption == true) {
                    allHotSearch.setValue(mUtils.getStringResource(R.string.s3_all));
                    allHotSearch.setCategory(searchMoreType);
                    allHotSearch.setId(-1);
                }

                try {
                    HotSearchFilterDataSource dataSource = new HotSearchFilterDataSource(getContext());
                    dataSource.open();

                    List<HotSearch> temp = dataSource.getRowsByIsShown(searchMoreType, HotSearch.Show.HIDE.getKey());

                    Log.d(LOGGER_TAG, "temp size: " + String.valueOf(temp.size()));

                    if(temp.size() > 0) {
                        if(isSingleSelect == false && showAllOption == true)
                            result.add(allHotSearch);

                        result.addAll(temp);
                    }
                    Log.d(LOGGER_TAG, "result size: " + String.valueOf(result.size()));

                    dataSource.close();

                } catch (Exception e) {

                }

                return result;
            }

            @Override
            protected void onPostExecute(List<HotSearch> hsl) {

                final List<HotSearch> hotSearchList = hsl;

                if(hotSearchList != null) {
                    final ListView ll_hot_search_more = (ListView) view.findViewById(R.id.ll_hot_search_more);
                    final HotSearchMoreRowAdapter adapter = new HotSearchMoreRowAdapter(getContext(), R.layout.list_label_check_image_row, isSelectedValueValue, isAllSelected, hotSearchList, categoryFilters);

                    Log.d(LOGGER_TAG, "Cat size: "+categoryFilters.size());

                    ll_hot_search_more.setAdapter(adapter);
                    ll_hot_search_more.setTextFilterEnabled(true);
                    ll_hot_search_more.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            HotSearch hotSearch = (HotSearch) parent.getAdapter().getItem(position);

                            ImageView iv = (ImageView) view.findViewById(R.id.iv_check);

                            if (isSingleSelect) { // if more list allows SINGLE SELECT ONLY
                                categoryFilters.clear();

                                for (int x = 0; x < parent.getAdapter().getCount(); x++) {
                                    View v = parent.getChildAt(x);
                                    try {
                                        ((ImageView) v.findViewById(R.id.iv_check)).setVisibility(View.INVISIBLE);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (iv.getVisibility() == View.INVISIBLE) {
                                    iv.setVisibility(View.VISIBLE);

                                    if (isSelectedValueValue) // selected value to be returned is the VALUE
                                        categoryFilters.add(hotSearch.getValue());
                                    else // selected value returned will be ID
                                        categoryFilters.add(String.valueOf(hotSearch.getId()));
                                }
                            } else { // MULTIPLE Select
                                if (hotSearch.getId() == -1) { // item selected is All
                                    if (iv.getVisibility() == View.INVISIBLE) { // currently ALL is unchecked, set all items to selected

                                        iv.setVisibility(View.VISIBLE);

                                        Log.d("parent adapter count: ", "" + parent.getAdapter().getCount());

                                        int num_of_visible_view = ll_hot_search_more.getLastVisiblePosition() - ll_hot_search_more.getFirstVisiblePosition();
//                                        for (int x = 0; x < parent.getAdapter().getCount(); x++) {
                                        for (int x = 1; x <= num_of_visible_view; x++) {
                                            View v = parent.getChildAt(x);

                                            HotSearch hs = (HotSearch) parent.getAdapter().getItem(x);
//                                            ((ImageView) v.findViewById(R.id.iv_check)).setVisibility(View.VISIBLE);
                                            ((ImageView) v.findViewById(R.id.iv_check)).setVisibility(View.INVISIBLE); // if all is checked, uncheck other checkmarks
                                        }

                                        for (HotSearch hs : hotSearchList) {
                                            String selVal = String.valueOf(hs.getId());
                                            if (isSelectedValueValue) { // selected value to be returned is the VALUE
                                                selVal = hs.getValue();
                                            }

                                            if (categoryFilters.contains(selVal) == false) {
                                                categoryFilters.add(selVal);
                                            }
                                        }
                                    } else {
                                        iv.setVisibility(View.INVISIBLE);

                                        int num_of_visible_view = ll_hot_search_more.getLastVisiblePosition() - ll_hot_search_more.getFirstVisiblePosition();
//                                        for (int x = 0; x < parent.getAdapter().getCount(); x++) {
                                        for (int x = 1; x <= num_of_visible_view; x++) {
                                            View v = parent.getChildAt(x);
                                            HotSearch hs = (HotSearch) parent.getAdapter().getItem(x);

                                            ((ImageView) v.findViewById(R.id.iv_check)).setVisibility(View.INVISIBLE);
                                        }

                                        for (HotSearch hs : hotSearchList) {
                                            String selVal = String.valueOf(hs.getId());
                                            if (isSelectedValueValue) { // selected value to be returned is the VALUE
                                                selVal = hs.getValue();
                                            }

                                            if (categoryFilters.contains(selVal) == true) {
                                                categoryFilters.remove(selVal);
                                            }
                                        }
                                    }

                                } else { // any other item

                                    if (iv.getVisibility() == View.VISIBLE) {
                                        iv.setVisibility(View.INVISIBLE);

                                        View v = parent.getChildAt(0);
                                        TextView firstChild = (TextView) v.findViewById(R.id.content);
                                        // set ALL check to invisible
                                        if(firstChild.getText().equals(mUtils.getStringResource(R.string.s3_all)))
                                            ((ImageView) v.findViewById(R.id.iv_check)).setVisibility(View.INVISIBLE);

                                        if (isSelectedValueValue) {
                                            categoryFilters.remove(hotSearch.getValue());
                                            // remove ALL
                                            categoryFilters.remove(mUtils.getStringResource(R.string.s3_all));
                                        } else {
                                            categoryFilters.remove(String.valueOf(hotSearch.getId()));
                                            // remove ALL
                                            categoryFilters.remove(String.valueOf(-1));
                                        }

                                    } else { // add selected item to list
                                        iv.setVisibility(View.VISIBLE);

                                        // uncheck ALL
                                        View v = parent.getChildAt(0);
                                        TextView firstChild = (TextView) v.findViewById(R.id.content);
                                        // set ALL check to invisible
                                        if(firstChild.getText().equals(mUtils.getStringResource(R.string.s3_all))) {
                                            ImageView imageViewCheck = (ImageView) v.findViewById(R.id.iv_check);
                                            if(imageViewCheck.getVisibility() == View.VISIBLE) {
                                                // clear catergory filters first
                                                categoryFilters.clear();
                                            }
                                            (imageViewCheck).setVisibility(View.INVISIBLE);
                                        }

                                        if (isSelectedValueValue)
                                            categoryFilters.remove(mUtils.getStringResource(R.string.s3_all));
                                        else
                                            categoryFilters.remove(String.valueOf(-1));

                                        if (isSelectedValueValue) {
                                            if(categoryFilters.contains(hotSearch.getValue()) == false)
                                                categoryFilters.add(hotSearch.getValue());
                                        } else {
                                            if(categoryFilters.contains(hotSearch.getId()) == false)
                                                categoryFilters.add(String.valueOf(hotSearch.getId()));
                                        }

                                        // set ALL check to visible if all items are selected
                                        if (categoryFilters.size() == hotSearchList.size() - 1) {
//                                            View v = parent.getChildAt(0);
                                            ((ImageView) v.findViewById(R.id.iv_check)).setVisibility(View.INVISIBLE);

                                            if (isSelectedValueValue)
                                                categoryFilters.add(mUtils.getStringResource(R.string.s3_all));
                                            else
                                                categoryFilters.add(String.valueOf(-1));
                                        }
                                    }
                                }
                            }

                            Log.d(LOGGER_TAG, categoryFilters.toString());
                        }
                    });

                    tv_hot_search_string.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            adapter.getFilter().filter(s.toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                }
            }
        };
        task.execute();

        ImageButton btn_back = (ImageButton) view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
//                    new SharedPref(getContext()).setSelectedMoreHotSearchFilters(searchMoreType, categoryFilters);
//                    mListener.goBack();
                    mListener.goBackHotSearchMore(searchMoreType, categoryFilters);
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
        void goBack();
        void goBackHotSearchMore(int type, List<String> selectedValues);
    }
}
