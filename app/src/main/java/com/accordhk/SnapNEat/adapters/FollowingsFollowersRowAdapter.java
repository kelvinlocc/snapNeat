package com.accordhk.SnapNEat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.models.User;
import com.accordhk.SnapNEat.utils.CustomFontTextView;
import com.accordhk.SnapNEat.utils.VolleySingleton;
import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jm on 3/2/16.
 */
public class FollowingsFollowersRowAdapter extends ArrayAdapter<User> {

    private static String LOGGER_TAG = "FollowingsFollowersRowAdapter";

    ImageLoader mImageLoader;

    public FollowingsFollowersRowAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RowHolder holder;

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_followings_followers_row, parent, false);

            holder = new RowHolder();
            holder.profile_pic = (CircleImageView) convertView.findViewById(R.id.profile_pic);
            holder.username = (CustomFontTextView) convertView.findViewById(R.id.username);

            convertView.setTag(holder);
        } else {
            holder = (RowHolder) convertView.getTag();
        }

        User row = getItem(position);

        mImageLoader = VolleySingleton.getInstance(getContext()).getImageLoader();

        mImageLoader.get(row.getAvatarThumbnail(), ImageLoader.getImageListener(holder.profile_pic, R.drawable.s1_bg_profile_pic, R.drawable.s1_bg_profile_pic));
        holder.username.setText(row.getUsername());

        return convertView;
    }

    static class RowHolder {
        private CircleImageView profile_pic;
        private CustomFontTextView username;
    }
}
