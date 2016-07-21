package com.accordhk.SnapNEat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.models.Snap;
import com.accordhk.SnapNEat.utils.CustomFontTextView;
import com.accordhk.SnapNEat.utils.VolleySingleton;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jm on 3/2/16.
 */
public class SnapListAdapter extends ArrayAdapter<Snap> {

    private static String LOGGER_TAG = "SnapListAdapter";

    ImageLoader mImageLoader;

    public SnapListAdapter(Context context, int resource, List<Snap> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RowHolder holder;

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_search_result_by_hash, parent, false);

            holder = new RowHolder();
            holder.iv_result_image = (NetworkImageView) convertView.findViewById(R.id.iv_result_image);
            holder.tv_snap_title = (TextView) convertView.findViewById(R.id.tv_snap_title);
            holder.profile_pic = (CircleImageView) convertView.findViewById(R.id.profile_pic);
            holder.username = (CustomFontTextView) convertView.findViewById(R.id.username);
            holder.tv_district = (CustomFontTextView) convertView.findViewById(R.id.tv_district);

            convertView.setTag(holder);
        } else {
            holder = (RowHolder) convertView.getTag();
        }

        Snap row = getItem(position);

        mImageLoader = VolleySingleton.getInstance(getContext()).getImageLoader();

        holder.iv_result_image.setImageUrl(row.getImage(), mImageLoader);
//        mImageLoader.get(row.getUser().getAvatarThumbnail(), ImageLoader.getImageListener(holder.iv_result_image, R.drawable.rounded_corner_image_view_bg, R.drawable.rounded_corner_image_view_bg));
        mImageLoader.get(row.getUser().getAvatarThumbnail(), ImageLoader.getImageListener(holder.profile_pic, R.drawable.s1_bg_profile_pic, R.drawable.s1_bg_profile_pic));
        holder.tv_snap_title.setText(row.getTitle());
        holder.username.setText(row.getUser().getUsername());
        if(row.getDistrict() != null)
            holder.tv_district.setText(row.getDistrict().getName());

        return convertView;
    }

    static class RowHolder {
        private NetworkImageView iv_result_image;
        private TextView tv_snap_title;
        private CircleImageView profile_pic;
        private CustomFontTextView username;
        private CustomFontTextView tv_district;
    }
}
