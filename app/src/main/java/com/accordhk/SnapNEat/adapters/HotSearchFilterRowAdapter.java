package com.accordhk.SnapNEat.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.models.HotSearch;
import com.accordhk.SnapNEat.models.HotSearchRow;
import com.accordhk.SnapNEat.utils.CustomFontTextView;
import com.accordhk.SnapNEat.utils.SharedPref;

import java.util.List;
import java.util.Map;

/**
 * Created by jm on 3/2/16.
 */
public class HotSearchFilterRowAdapter extends ArrayAdapter<HotSearchRow> {

    private static String LOGGER_TAG = "HotSearchFilterRowAdapter";

    private List<HotSearchRow> data;

    private HotSearchFilterRowAdapterInterface mListener;

    private boolean isSingleSelect;
    private boolean isSelectedValueValue;
    private Map<Integer, List<String>> selectedValues;

    public interface HotSearchFilterRowAdapterInterface {
        void showHotSearchMore(List<String> selValues, int type, boolean isSingleSelect, boolean isSelectedValueValue);
        void hotSearchItemClicked(View v, HotSearch hotSearch);
    }

    /**
     *
     * @param context
     * @param resource
     * @param i isSingleSelect: true if single select, else, allow multiple
     * @param iv isSelectedValueValue: true if the returned selected value is VALUE, else, return value is ID
     * @param objects
     * @param l
     */
    public HotSearchFilterRowAdapter(Context context, int resource, boolean i, boolean iv, List<HotSearchRow> objects, Map<Integer, List<String>> selValues, HotSearchFilterRowAdapterInterface l) {
        super(context, resource, objects);
        mListener = l;
        data = objects;
        isSingleSelect = i;
        isSelectedValueValue = iv;
        selectedValues = selValues;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final RowHolder holder;
        final HotSearchRow row = getItem(position);

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_hot_search_row, parent, false);

            holder = new RowHolder();
            holder.iv_hot_search_icon = (ImageView) convertView.findViewById(R.id.iv_hot_search_icon);
            holder.iv_hot_search_label = (CustomFontTextView) convertView.findViewById(R.id.iv_hot_search_label);
            holder.btn_hot_search_more = (CustomFontTextView) convertView.findViewById(R.id.btn_hot_search_more);
            holder.gv_hotsearch = (GridView) convertView.findViewById(R.id.gv_hotsearch);

            int divValue = 2;
            if(row.getType() == HotSearch.Category.SPENDINGS.getKey())
                divValue = 3;

            int h = (int)Math.ceil(row.getList().size()/divValue);
            float mod = row.getList().size()%divValue;
            if(mod == 1)
                h++;

            Log.d(LOGGER_TAG, "DENSITY: "+getContext().getResources().getDisplayMetrics().density);
            holder.gv_hotsearch.getLayoutParams().height = (int) (h * 40 * getContext().getResources().getDisplayMetrics().density);

            holder.btn_hot_search_more.setVisibility(View.VISIBLE);
            if(row.getType() == HotSearch.Category.HASHTAGS.getKey()) {
                holder.btn_hot_search_more.setVisibility(View.INVISIBLE);
                convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorF8F7EB));
            }

            convertView.setTag(holder);
        } else {
            holder = (RowHolder) convertView.getTag();
        }

        holder.iv_hot_search_icon.setImageResource(row.getImage());
        holder.iv_hot_search_label.setText(row.getLabel());

        final int type = row.getType();

        holder.btn_hot_search_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.showHotSearchMore(selectedValues.get(type), type, isSingleSelect, isSelectedValueValue);
                }
            }
        });

        if(row.getType() == HotSearch.Category.SPENDINGS.getKey())
            holder.gv_hotsearch.setNumColumns(3);
        else
            holder.gv_hotsearch.setNumColumns(2);

        holder.gv_hotsearch.setAdapter(new HotSearchFilterRowItemAdapter(getContext(), R.layout.list_gv_hotsearch_row_item, isSelectedValueValue, row.getList(), new HotSearchFilterRowItemAdapter.HotSearchFilterRowItemAdapterInterface() {
            @Override
            public void hotSearchItemClicked(View v, HotSearch hotSearch) {
                mListener.hotSearchItemClicked(v, hotSearch);
            }
        }));

//        holder.gv_hotsearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//        });

        return convertView;
    }

    static class RowHolder {
        private ImageView iv_hot_search_icon;
        private TextView btn_hot_search_more;
        private TextView iv_hot_search_label;
        private GridView gv_hotsearch;
    }
}
