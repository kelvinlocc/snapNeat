package com.accordhk.SnapNEat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.utils.SharedPref;

import java.util.List;

/**
 * Created by jm on 3/2/16.
 */
public class SettingsLanguageRowAdapter extends ArrayAdapter<String> {

    private static String LOGGER_TAG = "SettingsLanguageRowAdapter";
    private int currentLang;

    public SettingsLanguageRowAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        currentLang = new SharedPref(getContext()).getSelectedLanguage();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        String lang = getItem(position);
        RowHolder holder;

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.list_label_check_image_row, null);

            holder = new RowHolder();
            holder.content = (TextView) convertView.findViewById(R.id.content);
            holder.iv_check = (ImageView) convertView.findViewById(R.id.iv_check);

            convertView.setTag(holder);
        } else
            holder = (RowHolder) convertView.getTag();

        holder.content.setText(lang);
        holder.iv_check.setVisibility(View.INVISIBLE);

        if(currentLang == position)
            holder.iv_check.setVisibility(View.VISIBLE);


        return convertView;
    }

    static class RowHolder {
        private TextView content;
        private ImageView iv_check;
    }
}
