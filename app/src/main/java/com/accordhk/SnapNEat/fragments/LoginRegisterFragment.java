package com.accordhk.SnapNEat.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.accordhk.SnapNEat.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginRegisterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginRegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginRegisterFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String SHOW_LOGIN_FLAG = "showLoginFlag";

    // TODO: Rename and change types of parameters
    private boolean showLoginFlag;

    private OnFragmentInteractionListener mListener;

    Fragment mFragment;
    FragmentTransaction mTransaction;

    public LoginRegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment LoginRegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginRegisterFragment newInstance(boolean param1) {
        LoginRegisterFragment fragment = new LoginRegisterFragment();
        Bundle args = new Bundle();
        args.putBoolean(SHOW_LOGIN_FLAG, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            showLoginFlag = getArguments().getBoolean(SHOW_LOGIN_FLAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_register, container, false);

        Button btn_tab_login = (Button) view.findViewById(R.id.btn_tab_login);
        btn_tab_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.selectLoginRegisterFragment(true);
                }
            }
        });

        Button btn_tab_register = (Button) view.findViewById(R.id.btn_tab_register);
        btn_tab_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.selectLoginRegisterFragment(false);
                }
            }
        });

        ImageButton btn_back = (ImageButton) view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null)
                    mListener.goBack();
            }
        });

        if(view.findViewById(R.id.login_register_frame_content) != null) {
            if (savedInstanceState != null) {
                return view;
            }

            mTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            if(showLoginFlag) {
                mFragment = new LoginFragment();
            } else {
                mFragment = new RegisterFragment();
            }

            mTransaction.add(R.id.login_register_frame_content, mFragment);
            mTransaction.commit();
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
        void selectLoginRegisterFragment(boolean showLoginFlag);
    }
}
