package com.accordhk.SnapNEat.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.adapters.SettingsLanguageRowAdapter;
import com.accordhk.SnapNEat.utils.CustomFontTextView;
import com.accordhk.SnapNEat.utils.SharedPref;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsLanguageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsLanguageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsLanguageFragment extends BaseFragment {
    public static final String CURRENT_LANG = "currentLanguage";

    private OnFragmentInteractionListener mListener;

    public SettingsLanguageFragment() {
        // Required empty public constructor
    }

    private int currentLang;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment SettingsLanguageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsLanguageFragment newInstance(int param1) {
        SettingsLanguageFragment fragment = new SettingsLanguageFragment();
        Bundle args = new Bundle();
        args.putInt(CURRENT_LANG, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentLang = getArguments().getInt(CURRENT_LANG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings_language, container, false);

        final ListView lv_languages = (ListView) view.findViewById(R.id.lv_languages);

        CustomFontTextView action_bar_title = (CustomFontTextView) view.findViewById(R.id.action_bar_title);
        action_bar_title.setText(mUtils.getStringResource(R.string.a6_settings));

        ImageButton btn_back = (ImageButton) view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) {
                    mListener.goBack();
                }
            }
        });

        final List<String> langs = new ArrayList<>();
        langs.add("English");
        langs.add("繁體中文");
        langs.add("简体中文");

        SettingsLanguageRowAdapter adapter = new SettingsLanguageRowAdapter(getContext(), R.layout.list_label_check_image_row, langs);
        lv_languages.setAdapter(adapter);

        lv_languages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for(int x = 0; x < langs.size(); x++) {
                    View v = lv_languages.getChildAt(x);
                    ((ImageView) v.findViewById(R.id.iv_check)).setVisibility(View.INVISIBLE);
                }

                ImageView iv = (ImageView) view.findViewById(R.id.iv_check);
                iv.setVisibility(View.VISIBLE);

                Locale locale = mUtils.getLocale(position);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;

                new SharedPref(getContext()).setSelectedLanguage(position);

                getResources().updateConfiguration(config, getResources().getDisplayMetrics());

                // call this when the language setting is updated from Settings-Language page
                // updates the navigatonview
                getActivity().onConfigurationChanged(config);
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
    }
}
