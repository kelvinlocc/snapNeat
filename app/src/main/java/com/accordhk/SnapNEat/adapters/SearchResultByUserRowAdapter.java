package com.accordhk.SnapNEat.adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.models.Snap;
import com.accordhk.SnapNEat.utils.VolleySingleton;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

/**
 * Created by jm on 9/2/16.
 */
public class SearchResultByUserRowAdapter extends ArrayAdapter<Snap> {
    private static String LOGGER_TAG = "SearchResultByUserRowAdapter";

    ImageLoader mImageLoader;

    public SearchResultByUserRowAdapter(Context context, int resource, List<Snap> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Snap snap = getItem(position);

        mImageLoader = VolleySingleton.getInstance(getContext()).getImageLoader();

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_search_result_by_user_row_item, parent, false);
        }

        convertView.setTag(snap.getId());

        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();

        NetworkImageView snap_photo = (NetworkImageView) convertView.findViewById(R.id.snap_photo);
        snap_photo.getLayoutParams().height = metrics.widthPixels / 3;
        snap_photo.getLayoutParams().width = metrics.widthPixels / 3;
        snap_photo.setImageUrl(snap.getImageThumbnail(), mImageLoader);
//        mImageLoader.get(snap.getImageThumbnail(), ImageLoader.getImageListener(snap_photo, R.drawable.rounded_corner_image_view_bg, R.drawable.rounded_corner_image_view_bg));

        return convertView;
    }
}
