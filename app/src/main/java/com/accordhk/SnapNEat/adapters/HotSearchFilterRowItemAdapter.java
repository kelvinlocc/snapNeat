package com.accordhk.SnapNEat.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.models.HotSearch;
import com.accordhk.SnapNEat.utils.CustomFontTextView;
import com.accordhk.SnapNEat.utils.SharedPref;

import java.util.List;

/**
 * Created by jm on 3/2/16.
 */
public class HotSearchFilterRowItemAdapter extends ArrayAdapter<HotSearch> {

    private static String LOGGER_TAG = "HotSearchFilterRowItemAdapter";
    private boolean isSelectedValueValue;
    private List<String> category;
    private HotSearch row;

    private HotSearchFilterRowItemAdapterInterface mListener;

    public interface HotSearchFilterRowItemAdapterInterface {
        void hotSearchItemClicked(View v, HotSearch hotSearch);
    }

    public HotSearchFilterRowItemAdapter(Context context, int resource, boolean iv, List<HotSearch> objects, HotSearchFilterRowItemAdapterInterface l) {
        super(context, resource, objects);
        isSelectedValueValue = iv;
        mListener = l;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final RowHolder holder;

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_gv_hotsearch_row_item, parent, false);

            holder = new RowHolder();
            holder.tv_hot_search_item = (CustomFontTextView) convertView.findViewById(R.id.tv_hot_search_item);

            convertView.setTag(holder);
        } else {
            holder = (RowHolder) convertView.getTag();
        }

        row = getItem(position);

        final SharedPref sharedPref = new SharedPref(getContext());

//        category = sharedPref.getSelectedMoreHotSearchFilters(row.getCategory());
        String selVal = String.valueOf(row.getId());

        if(isSelectedValueValue) { // selected value returned is VALUE
            selVal = row.getValue();
        }

//        if(category.contains(selVal)) { // currently selected
//            holder.tv_hot_search_item.setBackgroundResource(R.drawable.bg_hot_search_item_sel);
//            holder.tv_hot_search_item.setTextColor(ContextCompat.getColor(getContext(),R.color.colorPrimary));
//        } else {
            holder.tv_hot_search_item.setBackgroundResource(R.drawable.bg_hot_search_item);
            holder.tv_hot_search_item.setTextColor(ContextCompat.getColor(getContext(),R.color.colorText));
//        }

//        holder.tv_hot_search_item.setTag(row.getType());
//        holder.tv_hot_search_item.setTag(row.getCategory());
        holder.tv_hot_search_item.setTag(row);
        holder.tv_hot_search_item.setText(row.getValue());

        holder.tv_hot_search_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HotSearch selItem = (HotSearch) holder.tv_hot_search_item.getTag();
                mListener.hotSearchItemClicked(v, selItem);

//                String selVal1 = String.valueOf(selItem.getId());
//
//                if(isSelectedValueValue) { // selected value returned is VALUE
//                    selVal1 = selItem.getValue();
//                }
//
//                if(category.contains(selVal1)) { // currently selected
//                    Log.d(selVal1, "Currently selected. remove: "+selVal1);
//                    category.remove(selVal1);
//                    v.setBackgroundResource(R.drawable.bg_hot_search_item);
//                    holder.tv_hot_search_item.setTextColor(ContextCompat.getColor(getContext(), R.color.colorText));
//                } else { // currently not selected
//                    Log.d(selVal1, "Currently not selected. add: "+selVal1);
//                    category.add(selVal1);
//                    v.setBackgroundResource(R.drawable.bg_hot_search_item_sel);
//                    holder.tv_hot_search_item.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
//                }
//
//                Log.d("Hotsearch row selected", "row type: "+selItem.getCategory());
//                sharedPref.setSelectedMoreHotSearchFilters(selItem.getCategory(), category);

            }
        });

        return convertView;
    }

    static class RowHolder {
        private TextView tv_hot_search_item;
    }
}
