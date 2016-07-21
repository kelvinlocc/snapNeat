package com.accordhk.SnapNEat.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.adapters.ReportInappropriateRowAdapter;
import com.accordhk.SnapNEat.models.BaseResponse;
import com.accordhk.SnapNEat.models.ReasonInappropriate;
import com.accordhk.SnapNEat.models.ResponseReasonInappropriate;
import com.accordhk.SnapNEat.services.ApiWebServices;
import com.accordhk.SnapNEat.utils.Constants;
import com.accordhk.SnapNEat.utils.CustomFontEditText;
import com.accordhk.SnapNEat.utils.CustomFontTextView;
import com.accordhk.SnapNEat.utils.VolleySingleton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReportInappropriateFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReportInappropriateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportInappropriateFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String USER_ID = "userId";
    public static final String SNAP_ID = "snapId";

    // TODO: Rename and change types of parameters
    private int userId;
    private int snapId;
    private int reasonId;
    private int lastItemPosition;

    private OnFragmentInteractionListener mListener;

    public ReportInappropriateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ReportInappropriateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportInappropriateFragment newInstance(int param1) {
        ReportInappropriateFragment fragment = new ReportInappropriateFragment();
        Bundle args = new Bundle();
        args.putInt(SNAP_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt(USER_ID);
            snapId = getArguments().getInt(SNAP_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report_inappropriate, container, false);

        ImageButton btn_back = (ImageButton) view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.goBack();
                }
            }
        });

        CustomFontTextView action_bar_title = (CustomFontTextView) view.findViewById(R.id.action_bar_title);
        action_bar_title.setText(mUtils.getStringResource(R.string.p6_report_inappropriate));

        CustomFontTextView tv_specify_label = (CustomFontTextView) view.findViewById(R.id.tv_specify_label);
        tv_specify_label.setText(mUtils.getStringResource(R.string.p6_report_inappropriate));

        final CustomFontEditText ed_reason_others = (CustomFontEditText) view.findViewById(R.id.ed_reason_others);
        final ListView ll_reasons = (ListView) view.findViewById(R.id.ll_reasons);

        final CustomFontTextView btn_submit = (CustomFontTextView) view.findViewById(R.id.btn_submit);
        btn_submit.setText(mUtils.getStringResource(R.string.p6_submit));

        try {
            Map<String, String> params = mUtils.getBaseRequestMap();

            mProgressDialog.show();
            mApi.getReasonInappropriate(params, mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
                @Override
                public void onResponse(Object object) {
                    try {
                        ResponseReasonInappropriate response = (ResponseReasonInappropriate) object;
                        mUtils.dismissDialog(mProgressDialog);

                        if (response != null) {
                            if(response.getStatus() == Constants.RES_UNAUTHORIZED) {
                                if (mListener != null) {
                                    mListener.showStartingFragmentFromLogout();
                                }
                            } else if (response.getStatus() != Constants.RES_SUCCESS) {
                                mUtils.getErrorDialog(response.getMessage()).show();
                            } else {
                                ReportInappropriateRowAdapter adapter = new ReportInappropriateRowAdapter(getContext(), R.layout.list_label_check_image_row, response.getResults());
                                ll_reasons.setAdapter(adapter);
                                ll_reasons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        reasonId = ((ReasonInappropriate) ll_reasons.getAdapter().getItem(position)).getId();

                                        lastItemPosition = position;

                                        for (int i = 0; i < ll_reasons.getAdapter().getCount(); i++) {
                                            View v = parent.getChildAt(i);
                                            (v.findViewById(R.id.iv_check)).setVisibility(View.INVISIBLE);
                                            ((TextView)(v.findViewById(R.id.content))).setTextColor(ContextCompat.getColor(getContext(), R.color.colorText));
                                        }

                                        (view.findViewById(R.id.iv_check)).setVisibility(View.VISIBLE);
                                        ((TextView)(view.findViewById(R.id.content))).setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                                    }
                                });

                                btn_submit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String error = "";
                                        String reasonOthers = "";

                                        reasonOthers = ed_reason_others.getText().toString().trim();

//                                        if (lastItemPosition == ll_reasons.getAdapter().getCount() - 1) { // selected other reason
//                                            if (reasonOthers.isEmpty()) {
//                                                error = String.format(mUtils.getStringResource(R.string.error_empty), ((ReasonInappropriate) ll_reasons.getItemAtPosition(lastItemPosition)).getName());
//                                            }
//                                        }

                                        Map<String, String> params = mUtils.getBaseRequestMap();
                                        params.put(ReasonInappropriate.SNAP_ID, String.valueOf(snapId));
                                        params.put(ReasonInappropriate.REASON_ID, String.valueOf(reasonId));

                                        if (reasonOthers.isEmpty() == false)
                                            params.put(ReasonInappropriate.DESCRIPTION, reasonOthers);

                                        try {
                                            mProgressDialog.show();
                                            mApi.postReportInappropriate(params, mUtils.generateAuthHeader(), new ApiWebServices.ApiListener() {
                                                @Override
                                                public void onResponse(Object object) {
                                                    try {
                                                        BaseResponse res = (BaseResponse) object;
                                                        mUtils.dismissDialog(mProgressDialog);
                                                        if (res != null) {
                                                            if(res.getStatus() == Constants.RES_UNAUTHORIZED) {
                                                                if (mListener != null) {
                                                                    mListener.showStartingFragmentFromLogout();
                                                                }
                                                            } else if (res.getStatus() != Constants.RES_SUCCESS) {
                                                                mUtils.getErrorDialog(res.getMessage()).show();
                                                            } else {
                                                                if (mListener != null)
                                                                    mListener.showSnapDetails(snapId);
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
                }
            });
        } catch (Exception e) {
            mUtils.dismissDialog(mProgressDialog);
            e.printStackTrace();
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
        void goBack();
        void showSnapDetails(int id);
        void showStartingFragmentFromLogout();
    }
}
