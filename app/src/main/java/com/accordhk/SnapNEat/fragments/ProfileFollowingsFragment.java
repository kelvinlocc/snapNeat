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

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.adapters.SnapListAdapter;
import com.accordhk.SnapNEat.models.ResponseListSnaps;
import com.accordhk.SnapNEat.models.Snap;
import com.accordhk.SnapNEat.models.User;
import com.accordhk.SnapNEat.services.ApiWebServices;
import com.accordhk.SnapNEat.utils.Constants;
import com.accordhk.SnapNEat.utils.CustomFontTextView;
import com.accordhk.SnapNEat.utils.VolleySingleton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import java.util.Map;
import java.util.logging.Logger;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFollowingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFollowingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFollowingsFragment extends BaseSearchFragment {

    public static final String USER_ID = "userId";

    private int userId;

    private OnFragmentInteractionListener mListener;

    public ProfileFollowingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ProfileFollowingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFollowingsFragment newInstance(int param1) {
        ProfileFollowingsFragment fragment = new ProfileFollowingsFragment();
        Bundle args = new Bundle();
        args.putInt(USER_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt(USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_profile_followings, container, false);

        final GridView gv_followings = (GridView) view.findViewById(R.id.gv_followings);
        final CustomFontTextView message = (CustomFontTextView) view.findViewById(R.id.message);

        page = 1;

        try {
            Map<String, String> params = mUtils.getBaseRequestMap();
            params.put(Constants.STR_PAGE, String.valueOf(page));
            params.put(User.USER_ID, String.valueOf(userId));

            //mProgressDialog.show();
            mApi.getSnapsFollowings(params, mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
                @Override
                public void onResponse(Object object) {
                    try {
                        ResponseListSnaps snaps = (ResponseListSnaps) object;
                        page = snaps.getCurrentPageNo();

                        //mUtils.dismissDialog(mProgressDialog);

                        if(snaps.getStatus() == Constants.RES_UNAUTHORIZED) {
                            if (mListener != null) {
                                mListener.showStartingFragmentFromLogout();
                            }
                        } else if (snaps.getStatus() != Constants.RES_SUCCESS) {
                            gv_followings.setVisibility(View.GONE);
                            message.setVisibility(View.VISIBLE);
                            message.setText(snaps.getMessage());
                        } else {
                            if (snaps.getResults().size() > 0) {
                                gv_followings.setVisibility(View.VISIBLE);
                                message.setVisibility(View.GONE);

                                final SnapListAdapter adapter = new SnapListAdapter(getContext(), R.layout.list_search_result_by_hash, snaps.getResults());
                                gv_followings.setAdapter(adapter);
                                gv_followings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        if (mListener != null) {
                                            //mListener.showSnapDetails(((Snap) gv_followings.getSelectedItem()).getId());
                                            mListener.showSnapDetails(((Snap) gv_followings.getAdapter().getItem(position)).getId());
                                        }
                                    }
                                });

                                gv_followings.setOnScrollListener(new AbsListView.OnScrollListener() {

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
                                                //mProgressDialog.show();
                                                Map<String, String> params = mUtils.getBaseRequestMap();
                                                params.put(Constants.STR_PAGE, String.valueOf(page));
                                                params.put(User.USER_ID, String.valueOf(userId));

                                                mApi.getSnapsFollowings(params, mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
                                                    @Override
                                                    public void onResponse(Object object) {
                                                        try {
                                                            ResponseListSnaps listSnaps = (ResponseListSnaps) object;
                                                            page = listSnaps.getCurrentPageNo();

                                                            if(listSnaps.getStatus() == Constants.RES_UNAUTHORIZED) {
                                                                if (mListener != null) {
                                                                    mListener.showStartingFragmentFromLogout();
                                                                }
                                                            } else if (listSnaps.getResults().size() > 0) {
                                                                adapter.addAll(listSnaps.getResults());
                                                                adapter.notifyDataSetChanged();
                                                            }
                                                            //mUtils.dismissDialog(mProgressDialog);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        error.printStackTrace();
                                                        //mUtils.dismissDialog(mProgressDialog);
                                                        mUtils.getErrorDialog(mUtils.getStringResource(R.string.error_cannot_process_request)).show();
                                                    }
                                                });
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                //mUtils.dismissDialog(mProgressDialog);
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
                                //mUtils.dismissDialog(mProgressDialog);
                                //mUtils.getDialog(mUtils.getStringResource(R.string.error_no_data)).show();
                                gv_followings.setVisibility(View.GONE);
                                message.setVisibility(View.VISIBLE);
                                message.setText(snaps.getMessage());
                            }
                        }

                        //mUtils.dismissDialog(mProgressDialog);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    //mUtils.dismissDialog(mProgressDialog);
                    gv_followings.setVisibility(View.GONE);
                    message.setVisibility(View.VISIBLE);
                    message.setText(mUtils.getStringResource(R.string.error_cannot_process_request));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            //mUtils.dismissDialog(mProgressDialog);
            gv_followings.setVisibility(View.GONE);
            message.setVisibility(View.VISIBLE);
            message.setText(mUtils.getStringResource(R.string.error_cannot_process_request));
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
        void showStartingFragmentFromLogout();
    }
}
