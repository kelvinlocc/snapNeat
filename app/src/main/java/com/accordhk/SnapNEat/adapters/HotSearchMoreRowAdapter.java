package com.accordhk.SnapNEat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.models.HotSearch;
import com.accordhk.SnapNEat.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jm on 3/2/16.
 */
public class HotSearchMoreRowAdapter extends ArrayAdapter<HotSearch> implements Filterable {

    private static String LOGGER_TAG = "HotSearchMoreRowAdapter";

    List<String> selectedFilters;

    List<HotSearch> origData;
    List<HotSearch> filteredData;

    private boolean mIsSelectedValueValue;
    private boolean mIsAllSelected;

    private String allId;

    public HotSearchMoreRowAdapter(Context context, int resource, boolean isSelectedValueValue, boolean isAllSelected, List<HotSearch> objects, List<String> selFilters) {
        super(context, resource, objects);
        selectedFilters = selFilters;

        origData = objects;
        filteredData = objects;

        mIsSelectedValueValue = isSelectedValueValue;
        mIsAllSelected = isAllSelected;

        allId = "-1";
        if(mIsSelectedValueValue) {
            allId = new Utils(context).getStringResource(R.string.s3_all);
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        HotSearch hotSearch = getItem(position);
        RowHolder holder;

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.list_label_check_image_row, null);

            holder = new RowHolder();
            holder.content = (TextView) convertView.findViewById(R.id.content);
            holder.iv_check = (ImageView) convertView.findViewById(R.id.iv_check);

            convertView.setTag(holder);
        } else
            holder = (RowHolder) convertView.getTag();

        holder.content.setText(hotSearch.getValue());
        holder.content.setTag(String.valueOf(hotSearch.getId()));

        holder.iv_check.setVisibility(View.INVISIBLE);

        String selVal = String.valueOf(hotSearch.getId());
        if(mIsSelectedValueValue) {
            selVal = hotSearch.getValue();
        }

        if(selectedFilters.contains(selVal)) {
            holder.iv_check.setVisibility(View.VISIBLE);
        }

//        if(mIsAllSelected && hotSearch.getId() != -1) // if ALL is selected, and current row id is != -1
//            holder.iv_check.setVisibility(View.INVISIBLE);

        if (selectedFilters.contains(allId) && !selVal.equals(allId))
            holder.iv_check.setVisibility(View.INVISIBLE);

        return convertView;
    }

    static class RowHolder {
        private TextView content;
        private ImageView iv_check;
    }

    @Override
    public int getCount() {
//        return super.getCount();
        return filteredData.size();
    }

    @Override
    public HotSearch getItem(int position) {
//        return super.getItem(position);
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
//        return super.getItemId(position);
        return position;
    }

    @Override
    public Filter getFilter() {
//        return super.getFilter();
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
//                return null;

                FilterResults results = new FilterResults();

                //If there's nothing to filter on, return the original data for your list
                if(constraint == null || constraint.length() == 0)
                {
                    results.values = origData;
                    results.count = origData.size();
                }
                else
                {
                    List<HotSearch> filterResultsData = new ArrayList<HotSearch>();

                    for(HotSearch data : origData)
                    {
                        //In this loop, you'll filter through originalData and compare each item to charSequence.
                        //If you find a match, add it to your new ArrayList
                        //I'm not sure how you're going to do comparison, so you'll need to fill out this conditional
                        if(data.getValue().toLowerCase().contains(constraint.toString().toLowerCase()))
//                        if(data.getValue().toLowerCase().startsWith(constraint.toString().toLowerCase()))
                        {
                            filterResultsData.add(data);
                        }
                    }

                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredData = (List<HotSearch>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
