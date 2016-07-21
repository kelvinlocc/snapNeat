package com.accordhk.SnapNEat.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.adapters.SearchResultByUserAdapter;
import com.accordhk.SnapNEat.models.BaseResponse;
import com.accordhk.SnapNEat.models.HotSearch;
import com.accordhk.SnapNEat.models.ResponseFollowUser;
import com.accordhk.SnapNEat.models.ResponseListSnaps;
import com.accordhk.SnapNEat.models.ResponseListUsers;
import com.accordhk.SnapNEat.models.Snap;
import com.accordhk.SnapNEat.models.User;
import com.accordhk.SnapNEat.services.ApiWebServices;
import com.accordhk.SnapNEat.utils.Constants;
import com.accordhk.SnapNEat.utils.CustomFontEditText;
import com.accordhk.SnapNEat.utils.CustomFontTextView;
import com.accordhk.SnapNEat.utils.SharedPref;
import com.accordhk.SnapNEat.utils.Utils;
import com.accordhk.SnapNEat.utils.VolleySingleton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchResultsByUsersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchResultsByUsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResultsByUsersFragment extends BaseSearchFragment {
    private static String LOGGER_TAG = "SearchResultsByUsersFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private String filters;

    public SearchResultsByUsersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchResultsByUsersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchResultsByUsersFragment newInstance(String param1, String param2) {
        SearchResultsByUsersFragment fragment = new SearchResultsByUsersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

            searchString = getArguments().getString(SEARCH_STRING);
            searchType = getArguments().getInt(SEARCH_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.list_search_result_by_user, container, false);

        final CustomFontTextView message = (CustomFontTextView) view.findViewById(R.id.message);
        final ListView ll_result_users = (ListView) view.findViewById(R.id.ll_result_users);

        Map<String, String> params = mUtils.getBaseRequestMap();
        params.put(Constants.STR_PAGE, String.valueOf(page));
        params.put(SEARCH_STRING, searchString);

        // get selected categories
//        SharedPref sharedPref = new SharedPref(getContext());
//        List<String> cat = sharedPref.getSelectedMoreHotSearchFilters(HotSearch.Category.DISTRICT.getKey());
//
//        filters = android.text.TextUtils.join(",", cat);
//
//        cat = sharedPref.getSelectedMoreHotSearchFilters(HotSearch.Category.DISH.getKey());
//        if(filters.isEmpty() == false && cat.size() > 0) {
//            filters += ","+android.text.TextUtils.join(",", cat);
//        }
//
//        cat = sharedPref.getSelectedMoreHotSearchFilters(HotSearch.Category.SPENDINGS.getKey());
//        if(filters.isEmpty() == false && cat.size() > 0) {
//            filters += ","+android.text.TextUtils.join(",", cat);
//        }
//
//        params.put(SEARCH_CATEGORY_FILTERS, filters);

        final User user = new SharedPref(mContext).getLoggedInUser();
        if(user != null) {
            params.put(Snap.USER_ID, String.valueOf(user.getId()));
        }

        page = 1;

        try {
            mProgressDialog.show();
            mApi.getSnapsByUser(params, new ApiWebServices.ApiListener() {
                @Override
                public void onResponse(Object object) {
                    try {
                        ResponseListUsers users = (ResponseListUsers) object;

                        if (users.getStatus() != Constants.RES_SUCCESS) {
                            mUtils.dismissDialog(mProgressDialog);
                            message.setVisibility(View.VISIBLE);
                            message.setText(users.getMessage());
                            ll_result_users.setVisibility(View.INVISIBLE);
                        } else {

                            if (users.getResults().size() > 0) {
                                page = users.getCurrentPageNo();

                                message.setVisibility(View.INVISIBLE);
                                ll_result_users.setVisibility(View.VISIBLE);
                                final SearchResultByUserAdapter adapter = new SearchResultByUserAdapter(getContext(), R.layout.list_search_result_by_user_row, users.getResults(), new SearchResultByUserAdapter.SearchResultByUserAdapterInterface() {
                                    @Override
                                    public void showUserProfile(int userId) {
                                        if (mListener != null) {
                                            mListener.showUserProfile(userId);
                                        }
                                    }

                                    @Override
                                    public void showSnapDetails(int id) {
                                        if (mListener != null) {
                                            mListener.showSnapDetails(id);
                                        }
                                    }

                                    @Override
                                    public void followUser(int userId, final View v) {

                                        User user1 = new SharedPref(getContext()).getLoggedInUser();
                                        if (user1 != null) {
                                            try {
                                                Map<String, String> followParams = mUtils.getBaseRequestMap();
                                                followParams.put(User.USER_ID, String.valueOf(userId));

                                                mApi.postFollowUser(followParams, mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
                                                    @Override
                                                    public void onResponse(Object object) {
                                                        try {
                                                            ResponseFollowUser response = (ResponseFollowUser) object;
                                                            if(response.getStatus() == Constants.RES_UNAUTHORIZED) {
                                                                if (mListener != null) {
                                                                    mListener.showStartingFragmentFromLogout();
                                                                }
                                                            } else if (response.getStatus() != Constants.RES_SUCCESS) {
                                                                mUtils.getErrorDialog(response.getMessage()).show();
                                                            } else {
                                                                ImageButton addUser = (ImageButton) v.findViewById(R.id.btn_add_user);
                                                                addUser.setVisibility(View.INVISIBLE);
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }

                                                    }

                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        error.printStackTrace();
                                                        mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_cannot_process_request)).show();
                                                    }
                                                });
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            if (mListener != null)
                                                mListener.showLoginRegistrationFragment(true);
                                        }
                                    }
                                });

                                ll_result_users.setAdapter(adapter);

                                ll_result_users.setOnScrollListener(new AbsListView.OnScrollListener() {

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
                                                params.put(SEARCH_STRING, searchString);
                                                params.put(SEARCH_CATEGORY_FILTERS, filters);

                                                User user = new SharedPref(mContext).getLoggedInUser();
                                                if (user != null) {
                                                    params.put(Snap.USER_ID, String.valueOf(user.getId()));
                                                }

                                                mProgressDialog.show();
                                                mApi.getSnapsByUser(params, new ApiWebServices.ApiListener() {
                                                    @Override
                                                    public void onResponse(Object object) {
                                                        try {
                                                            ResponseListUsers users = (ResponseListUsers) object;

                                                            page = users.getCurrentPageNo();

                                                            if (users.getResults().size() > 0) {
                                                                adapter.addAll(users.getResults());
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
                                                        error.printStackTrace();
                                                        mUtils.dismissDialog(mProgressDialog);
                                                        mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_cannot_process_request)).show();
                                                    }
                                                });
                                            } catch (Exception e) {
                                                e.printStackTrace();
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
                                mUtils.dismissDialog(mProgressDialog);
                            } else {
                                mUtils.dismissDialog(mProgressDialog);
                                message.setVisibility(View.VISIBLE);
                                message.setText(users.getMessage());
                                ll_result_users.setVisibility(View.INVISIBLE);
                            }
                        }
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
            e.printStackTrace();
        }

        TextView tv_hot_search_string = (TextView) view.findViewById(R.id.tv_hot_search_string);
        tv_hot_search_string.setText(searchString);
        tv_hot_search_string.setFocusable(false);

        ImageButton btn_back = (ImageButton) view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) {
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
        void showLoginRegistrationFragment(boolean b);
        void showUserProfile(int userId);
        void goBack();
        void showStartingFragmentFromLogout();
    }
}
