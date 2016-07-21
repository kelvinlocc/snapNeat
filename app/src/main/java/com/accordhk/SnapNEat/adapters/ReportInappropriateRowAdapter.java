package com.accordhk.SnapNEat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.models.ReasonInappropriate;

import java.util.List;

/**
 * Created by jm on 3/2/16.
 */
public class ReportInappropriateRowAdapter extends ArrayAdapter<ReasonInappropriate> implements Filterable {

    private static String LOGGER_TAG = "ReportInappropriateRowAdapter";

    public ReportInappropriateRowAdapter(Context context, int resource, List<ReasonInappropriate> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ReasonInappropriate reasonInappropriate = getItem(position);
        RowHolder holder;

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.list_label_check_image_row, null);

            holder = new RowHolder();
            holder.content = (TextView) convertView.findViewById(R.id.content);
            holder.iv_check = (ImageView) convertView.findViewById(R.id.iv_check);

            convertView.setTag(holder);
        } else
            holder = (RowHolder) convertView.getTag();

        holder.content.setText(reasonInappropriate.getName());
        holder.iv_check.setVisibility(View.INVISIBLE);
//        if (selectedFilters.contains(String.valueOf(hotSearch.getId())))
//            holder.iv_check.setVisibility(View.VISIBLE);

        return convertView;
    }

    static class RowHolder {
        private TextView content;
        private ImageView iv_check;
    }
}
