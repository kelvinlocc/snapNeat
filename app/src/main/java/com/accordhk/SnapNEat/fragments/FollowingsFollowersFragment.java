package com.accordhk.SnapNEat.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.adapters.FollowingsFollowersRowAdapter;
import com.accordhk.SnapNEat.models.ResponseListSnaps;
import com.accordhk.SnapNEat.models.ResponseListUsers;
import com.accordhk.SnapNEat.models.User;
import com.accordhk.SnapNEat.services.ApiWebServices;
import com.accordhk.SnapNEat.utils.Constants;
import com.accordhk.SnapNEat.utils.CustomFontTextView;
import com.accordhk.SnapNEat.utils.Utils;
import com.accordhk.SnapNEat.utils.VolleySingleton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FollowingsFollowersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FollowingsFollowersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FollowingsFollowersFragment extends BaseSearchFragment {

    public static final int TYPE_FOLLOWER = 0;
    public static final int TYPE_FOLLOWING = 1;

    public static final String TYPE = "type";
    public static final String USER_ID = "userId";

    // TODO: Rename and change types of parameters
    private int type;
    private int userId;

    private OnFragmentInteractionListener mListener;

    public FollowingsFollowersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FollowingsFollowersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FollowingsFollowersFragment newInstance(int param1, int param2) {
        FollowingsFollowersFragment fragment = new FollowingsFollowersFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, param1);
        args.putInt(USER_ID, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(TYPE);
            userId = getArguments().getInt(USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_followings_followers, container, false);

        final ListView ll_followings_followers = (ListView) view.findViewById(R.id.ll_followings_followers);

        CustomFontTextView action_bar_title = (CustomFontTextView) view.findViewById(R.id.action_bar_title);
        if(type == TYPE_FOLLOWER)
            action_bar_title.setText(mUtils.getStringResource(R.string.p5_followers));
        else
            action_bar_title.setText(mUtils.getStringResource(R.string.p5_followings));

        ImageButton btn_back = (ImageButton) view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.goBack();
                }
            }
        });

        page = 1;

        switch (type) {
            case TYPE_FOLLOWER:

                try {
                    Map<String, String> params = mUtils.getBaseRequestMap();
                    params.put(Constants.STR_PAGE, String.valueOf(page));
                    params.put(User.USER_ID, String.valueOf(userId));

                    mProgressDialog.show();
                    mApi.getMyFollowers(params, mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
                        @Override
                        public void onResponse(Object object) {
                            try {
                                mUtils.dismissDialog(mProgressDialog);
                                ResponseListUsers users = (ResponseListUsers) object;

                                if(users.getStatus() == Constants.RES_UNAUTHORIZED) {
                                    if (mListener != null) {
                                        mListener.showStartingFragmentFromLogout();
                                    }
                                } else if (users.getStatus() != Constants.RES_SUCCESS) {
                                    mUtils.getErrorDialog(users.getMessage()).show();
                                } else {
                                    page = users.getCurrentPageNo();

                                    if (users.getResults().isEmpty() == false) {
                                        final FollowingsFollowersRowAdapter adapter = new FollowingsFollowersRowAdapter(getContext(), R.layout.list_followings_followers_row, users.getResults());

                                        ll_followings_followers.setAdapter(adapter);

                                        ll_followings_followers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                User user = (User) ll_followings_followers.getItemAtPosition(position);

                                                if (mListener != null) {
                                                    mListener.showUserProfile(user.getId());
                                                }
                                            }
                                        });

                                        ll_followings_followers.setOnScrollListener(new AbsListView.OnScrollListener() {

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

                                                        mProgressDialog.show();
                                                        Map<String, String> params = mUtils.getBaseRequestMap();
                                                        params.put(Constants.STR_PAGE, String.valueOf(page));

                                                        mApi.getMyFollowers(params, mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
                                                            @Override
                                                            public void onResponse(Object object) {
                                                                try {
                                                                    ResponseListUsers users = (ResponseListUsers) object;
                                                                    page = users.getCurrentPageNo();

                                                                    mUtils.dismissDialog(mProgressDialog);

                                                                    if(users.getStatus() == Constants.RES_UNAUTHORIZED) {
                                                                        if(mListener != null) {
                                                                            mListener.showStartingFragmentFromLogout();
                                                                        }
                                                                    } else if (users.getResults().size() > 0) {
                                                                        adapter.addAll(users.getResults());
                                                                        adapter.notifyDataSetChanged();
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
                                                }
                                            }

                                            @Override
                                            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                                                this.currentFirstVisibleItem = firstVisibleItem;
                                                this.currentVisibleItemCount = visibleItemCount;
                                                this.totalItem = totalItemCount;
                                            }
                                        });
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
                    mUtils.dismissDialog(mProgressDialog);
                }

                break;

            case TYPE_FOLLOWING:
                try {
                    mProgressDialog.show();

                    Map<String, String> params = mUtils.getBaseRequestMap();
                    params.put(Constants.STR_PAGE, String.valueOf(page));
                    params.put(User.USER_ID, String.valueOf(userId));

                    mApi.getMyFollowings(params, mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
                        @Override
                        public void onResponse(Object object) {
                            try {
                                mUtils.dismissDialog(mProgressDialog);
                                ResponseListUsers users = (ResponseListUsers) object;

                                if(users.getStatus() == Constants.RES_UNAUTHORIZED) {
                                    if (mListener != null) {
                                        mListener.showStartingFragmentFromLogout();
                                    }
                                } else if(users.getStatus() != Constants.RES_SUCCESS) {
                                    mUtils.getErrorDialog(users.getMessage()).show();
                                } else {
                                    page = users.getCurrentPageNo();

                                    if(users.getResults().isEmpty() == false) {
                                        final FollowingsFollowersRowAdapter adapter = new FollowingsFollowersRowAdapter(getContext(), R.layout.list_followings_followers_row, users.getResults());

                                        ll_followings_followers.setAdapter(adapter);

                                        ll_followings_followers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                User user = (User) ll_followings_followers.getItemAtPosition(position);

                                                if (mListener != null) {
                                                    mListener.showUserProfile(user.getId());
                                                }
                                            }
                                        });

                                        ll_followings_followers.setOnScrollListener(new AbsListView.OnScrollListener() {

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
                                                        mProgressDialog.show();
                                                        Map<String, String> params = mUtils.getBaseRequestMap();
                                                        params.put(Constants.STR_PAGE, String.valueOf(page));

                                                        mApi.getMyFollowings(params, mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
                                                            @Override
                                                            public void onResponse(Object object) {
                                                                try {
                                                                    ResponseListUsers users = (ResponseListUsers) object;
                                                                    page = users.getCurrentPageNo();

                                                                    mUtils.dismissDialog(mProgressDialog);

                                                                    if(users.getStatus() == Constants.RES_UNAUTHORIZED) {
                                                                        if(mListener != null) {
                                                                            mListener.showStartingFragmentFromLogout();
                                                                        }
                                                                    } else if (users.getResults().size() > 0) {
                                                                        adapter.addAll(users.getResults());
                                                                        adapter.notifyDataSetChanged();
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
                    mUtils.dismissDialog(mProgressDialog);
                }

                break;
        }

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
    public void onPause() {
        super.onPause();
        VolleySingleton.getInstance(getContext()).getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
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
        void showUserProfile(int userId);
        void goBack();
        void showStartingFragmentFromLogout();
    }
}
