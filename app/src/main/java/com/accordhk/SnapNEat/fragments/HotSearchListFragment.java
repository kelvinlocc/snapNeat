package com.accordhk.SnapNEat.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.adapters.HotSearchFilterRowAdapter;
import com.accordhk.SnapNEat.dao.HotSearchFilterDataSource;
import com.accordhk.SnapNEat.models.HotSearch;
import com.accordhk.SnapNEat.models.HotSearchRow;
import com.accordhk.SnapNEat.services.ApiWebServices;
import com.accordhk.SnapNEat.utils.CustomFontTextView;
import com.accordhk.SnapNEat.utils.VolleySingleton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HotSearchListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HotSearchListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HotSearchListFragment extends BaseFragment {
    private static String LOGGER_TAG = "HotSearchListFragment";

    private OnFragmentInteractionListener mListener;

    public HotSearchListFragment() {
        // Required empty public constructor
    }

    private HashMap<Integer, List<String>> selectedValuesMap;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HotSearchListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HotSearchListFragment newInstance(String param1, String param2) {
        HotSearchListFragment fragment = new HotSearchListFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        selectedValuesMap = new HashMap<Integer, List<String>>();
        selectedValuesMap.put(HotSearch.Category.DISH.getKey(), new ArrayList<String>());
        selectedValuesMap.put(HotSearch.Category.DISTRICT.getKey(), new ArrayList<String>());
        selectedValuesMap.put(HotSearch.Category.SPENDINGS.getKey(), new ArrayList<String>());
        selectedValuesMap.put(HotSearch.Category.HASHTAGS.getKey(), new ArrayList<String>());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_hot_search_list, container, false);

        try {
            mProgressDialog.show();
            AsyncTask<Void, Void, List<HotSearchRow>> task = new AsyncTask<Void, Void, List<HotSearchRow>>() {
                @Override
                protected List<HotSearchRow> doInBackground(Void... params) {
                    List<HotSearchRow> rows = new ArrayList<HotSearchRow>();

                    String[] labels = {getResources().getString(R.string.s2_district), getResources().getString(R.string.s2_dish), getResources().getString(R.string.s2_spendings), getResources().getString(R.string.s2_hot_hashtags)};
                    int[] images = {R.drawable.s2_icon_district, R.drawable.s2_icon_dish, R.drawable.s2_icon_spendings, R.drawable.s2_icon_hashtags};

                    try {
                        HotSearchFilterDataSource dataSource = new HotSearchFilterDataSource(getContext());
                        dataSource.open();

                        for (int x = 0; x < labels.length; x++) {
                            HotSearchRow row = new HotSearchRow();
                            row.setList(dataSource.getRowsByIsShown(x, HotSearch.Show.SHOW.getKey()));
                            row.setType(x);
                            row.setLabel(labels[x]);
                            row.setImage(images[x]);
                            rows.add(row);
                        }

                        dataSource.close();
                    } catch (Exception e) {

                    }

                    return rows;
                }

                @Override
                protected void onPostExecute(List<HotSearchRow> list) {
                    mUtils.dismissDialog(mProgressDialog);
                    Log.d(LOGGER_TAG, "list size: " + list.size());

                    ListView listView = (ListView) view.findViewById(R.id.lv_hot_search);
                    HotSearchFilterRowAdapter adapter = new HotSearchFilterRowAdapter(getContext(), R.layout.list_hot_search_row, false, false, list, selectedValuesMap, new HotSearchFilterRowAdapter.HotSearchFilterRowAdapterInterface() {
                        @Override
                        public void showHotSearchMore(List<String> selValues, int type, boolean isSingleSelect, boolean isSelectedValueValue) {
                            mListener.showHotSearchMore(selValues, type, isSingleSelect, isSelectedValueValue);
                        }

                        @Override
                        public void hotSearchItemClicked(View v, HotSearch hotSearch) {
                            TextView textView = (TextView) v;
                            String selVal1 = String.valueOf(hotSearch.getId());

                            if(selectedValuesMap.get(hotSearch.getCategory()).contains(selVal1)) { // currently selected
                                Log.d(selVal1, "Currently selected. remove: "+selVal1);
                                selectedValuesMap.get(hotSearch.getCategory()).remove(selVal1);
                                textView.setBackgroundResource(R.drawable.bg_hot_search_item);
                                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorText));
                            } else { // currently not selected
                                Log.d(selVal1, "Currently not selected. add: "+selVal1);
                                selectedValuesMap.get(hotSearch.getCategory()).add(selVal1);
                                textView.setBackgroundResource(R.drawable.bg_hot_search_item_sel);
                                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                            }
                        }
                    });
                    listView.setAdapter(adapter);
                }
            };

            task.execute();

        } catch (Exception e) {
            e.printStackTrace();
            mUtils.dismissDialog(mProgressDialog);
        }

        CustomFontTextView action_bar_title = (CustomFontTextView) view.findViewById(R.id.action_bar_title);
        action_bar_title.setText(mUtils.getStringResource(R.string.s2_filter));

        ImageButton btn_back = (ImageButton) view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.goBack();
                }
            }
        });

        final TextView tv_hot_search_string = (TextView) view.findViewById(R.id.tv_hot_search_string);

        TextView btn_submit = (TextView) view.findViewById(R.id.btn_submit);
        btn_submit.setText(mUtils.getStringResource(R.string.s2_search));
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    Log.d(LOGGER_TAG, "btn_search clicked");

                    int type = HotSearch.SearchResultType.HASH.getKey();
                    String searchText = String.valueOf(tv_hot_search_string.getText()).trim();
                    if (searchText.contains("@") && searchText.contains("#"))
                        mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_not_allowed_search_by_user_hash)).show();
                    else {

                        String error = "";

                        // check if valid mention
                        if(searchText.contains("@")) {
                            type = HotSearch.SearchResultType.USER.getKey();
//                            error = mUtils.isValidMention(R.string.s12_hashtags, searchText, false);
//
//                            if(error.isEmpty() == false)
//                                mUtils.getErrorDialog(error).show();
                        } else {
//                            error = mUtils.isValidHashTag(R.string.s12_hashtags, searchText, false, true);
//
//                            if(error.isEmpty() == false)
//                                mUtils.getErrorDialog(error).show();
                        }

                        if(selectedValuesMap.get(HotSearch.Category.DISH.getKey()).isEmpty()
                                && selectedValuesMap.get(HotSearch.Category.DISTRICT.getKey()).isEmpty()
                                && selectedValuesMap.get(HotSearch.Category.SPENDINGS.getKey()).isEmpty()
                                && selectedValuesMap.get(HotSearch.Category.HASHTAGS.getKey()).isEmpty()
                                && searchText.isEmpty()) {
                            error = mUtils.getStringResource(R.string.s2_error_specify);
                        }

                        if(error.isEmpty()) {
                            if(mListener != null)
                                mListener.showSearchResults(type, searchText, selectedValuesMap);
                        } else {
                            mUtils.getErrorDialog(error).show();
                        }
                    }
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

    public void customResumeFromBack(int type, List<String> selectedValues) {
        selectedValuesMap.put(type, selectedValues);
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
        void showHotSearchMore(List<String> selValues, int type, boolean isSingleSelect, boolean isSelectedValueValue);
        void showSearchResults(int type, String searchString, HashMap<Integer, List<String>> selectedValuesMap);
        void goBack();
    }
}
