package com.accordhk.SnapNEat.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.models.Restaurant;
import com.accordhk.SnapNEat.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jm on 3/2/16.
 */
public class RestaurantsMoreRowAdapter extends ArrayAdapter<Restaurant> implements Filterable {

    private static String LOGGER_TAG = "HotSearchMoreRowAdapter";

    Restaurant selectedFilters;

    List<Restaurant> origData;
    List<Restaurant> filteredData;

    String mCurrentAddress;

    public RestaurantsMoreRowAdapter(Context context, int resource, List<Restaurant> objects, Restaurant selFilters, String defaultAddress) {
        super(context, resource, objects);
        selectedFilters = selFilters;

        origData = objects;
        filteredData = objects;
        mCurrentAddress = defaultAddress;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//        Restaurant restaurant = getItem(position);
        Restaurant restaurant = filteredData.get(position);
        RowHolder holder;

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.list_new_resto_check_image_row, null);

            holder = new RowHolder();
            holder.content = (TextView) convertView.findViewById(R.id.content);
            holder.tv_temptext1 = (TextView) convertView.findViewById(R.id.tv_temptext1);
            holder.tv_temptext2 = (TextView) convertView.findViewById(R.id.tv_temptext2);
            holder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            holder.iv_check = (ImageView) convertView.findViewById(R.id.iv_check);

            convertView.setTag(holder);
        } else
            holder = (RowHolder) convertView.getTag();

//        String val;
//        if(hotSearch.getCategory() == HotSearch.Category.SPENDINGS.getKey()) {
//            val = String.valueOf(hotSearch.getFrom())+" - "+String.valueOf(hotSearch.getTo());
//        } else {
//            val = hotSearch.getValue();
//        }
//
//        holder.content.setText(val);


        holder.tv_temptext1.setText("");
        holder.tv_temptext2.setText("");
        holder.content.setTextColor(ContextCompat.getColor(getContext(), R.color.colorText));
        holder.content.setText(restaurant.getName());
        holder.content.setTag(String.valueOf(restaurant.getId()));
        holder.tv_address.setText(restaurant.getLocation());

        holder.iv_check.setVisibility(View.INVISIBLE);

        if(selectedFilters != null) {
            if(selectedFilters.getId() == restaurant.getId())
                holder.iv_check.setVisibility(View.VISIBLE);
        }

        if(restaurant.getId() == -1) {
            holder.iv_check.setImageDrawable(getContext().getResources().getDrawable(R.drawable.s13a_btn_plus));
            holder.iv_check.setVisibility(View.VISIBLE);
            holder.content.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            holder.tv_temptext1.setText((new Utils(getContext())).getStringResource(R.string.s13a_add_text1));
            holder.tv_temptext2.setText((new Utils(getContext())).getStringResource(R.string.s13a_add_text2));
            holder.tv_address.setText(mCurrentAddress);
        }

        return convertView;
    }

    static class RowHolder {
        private TextView tv_temptext1;
        private TextView content;
        private TextView tv_temptext2;
        private TextView tv_address;
        private ImageView iv_check;
    }

    @Override
    public int getCount() {
//        return super.getCount();
        return filteredData.size();
    }

    @Override
    public Restaurant getItem(int position) {
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
                    List<Restaurant> filterResultsData = new ArrayList<Restaurant>();

                    for(Restaurant data : origData)
                    {
                        //In this loop, you'll filter through originalData and compare each item to charSequence.
                        //If you find a match, add it to your new ArrayList
                        //I'm not sure how you're going to do comparison, so you'll need to fill out this conditional
                        if(data.getName().toLowerCase().contains(constraint.toString().toLowerCase()))
//                        if(data.getName().toLowerCase().startsWith(constraint.toString().toLowerCase()))
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
                filteredData = (List<Restaurant>) results.values;

                if(filteredData.size() == 0) {
                    List<Restaurant> newRestoList = new ArrayList<Restaurant>();
                    Restaurant resto = new Restaurant();

//                    String name = (new Utils(getContext())).getStringResource(R.string.s13a_add);
//                    resto.setName(String.format(name, constraint.toString().trim()));
                    resto.setName(constraint.toString().trim());
                    resto.setDescription(constraint.toString().trim()); // place here first the typed restaurant name
                    resto.setId(-1);
                    newRestoList.add(resto);
                    filteredData = newRestoList;
                }

                notifyDataSetChanged();
            }
        };
    }
}
