package com.accordhk.SnapNEat.adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.models.District;
import com.accordhk.SnapNEat.utils.CustomFontTextView;

import java.util.List;

/**
 * Created by jm on 3/2/16.
 */
public class ProfileFootprintsRowAdapter extends ArrayAdapter<District> {

    private static String LOGGER_TAG = "ProfileFootprintsRowAdapter";

    private List<District> data;

    private ProfileFootprintsRowAdapterListener mListener;

    public interface ProfileFootprintsRowAdapterListener {
        void showSnapDetails(int snapId);
    }

    public ProfileFootprintsRowAdapter(Context context, int resource, List<District> objects, ProfileFootprintsRowAdapterListener l) {
        super(context, resource, objects);
        data = objects;
        mListener = l;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RowHolder holder;

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_footprints_row, parent, false);

            holder = new RowHolder();
            holder.tv_district = (CustomFontTextView) convertView.findViewById(R.id.tv_district);
            holder.gv_footprints = (GridView) convertView.findViewById(R.id.gv_footprints);

            convertView.setTag(holder);
        } else {
            holder = (RowHolder) convertView.getTag();
        }

        District row = getItem(position);

        holder.tv_district.setText(row.getName());

        final ProfileFootprintsRowSnapItemsAdapter adapter = new ProfileFootprintsRowSnapItemsAdapter(getContext(), R.layout.list_footprints_row, row.getSnaps(), new ProfileFootprintsRowSnapItemsAdapter.ProfileFootprintsRowSnapItemsAdapterListener() {
            @Override
            public void showSnapDetails(int snapId) {
                if(mListener != null) {
                    mListener.showSnapDetails(snapId);
                }
            }
        });
        holder.gv_footprints.setAdapter(adapter);


        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();

        int h = (int)Math.ceil(row.getSnaps().size()/4);
        float mod = row.getSnaps().size()%4;
        if(mod == 1)
            h++;

        if(h < 1)
            h = 1;

        //holder.gv_footprints.getLayoutParams().height = (int) h * (metrics.widthPixels / 4) + 10; // 10 is for the margin
        holder.gv_footprints.getLayoutParams().height = (int) (metrics.widthPixels / 2) + 15; // 15 is for the margin

        return convertView;
    }

    static class RowHolder {
        private CustomFontTextView tv_district;
        private GridView gv_footprints;
    }
}
