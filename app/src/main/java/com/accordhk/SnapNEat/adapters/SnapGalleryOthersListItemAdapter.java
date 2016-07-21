package com.accordhk.SnapNEat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.models.Snap;
import com.accordhk.SnapNEat.utils.CustomFontTextView;
import com.accordhk.SnapNEat.utils.VolleySingleton;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

/**
 * Created by jm on 3/2/16.
 */
public class SnapGalleryOthersListItemAdapter extends ArrayAdapter<Snap> {

    private static String LOGGER_TAG = "SnapGalleryOthersListItemAdapter";

    ImageLoader mImageLoader;

    private SnapGalleryOthersListItemAdapterListener mListener;

    public interface SnapGalleryOthersListItemAdapterListener {
        void showSnapDetails(int snapId);
    }

    public SnapGalleryOthersListItemAdapter(Context context, int resource, List<Snap> objects, SnapGalleryOthersListItemAdapterListener l) {
        super(context, resource, objects);
        mListener = l;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RowHolder holder;

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_gallery_others_item, parent, false);

            holder = new RowHolder();
            holder.iv_result_image = (NetworkImageView) convertView.findViewById(R.id.iv_result_image);
            holder.tv_snap_title = (CustomFontTextView) convertView.findViewById(R.id.tv_snap_title);
            holder.tv_district = (CustomFontTextView) convertView.findViewById(R.id.tv_district);

            convertView.setTag(holder);
        } else {
            holder = (RowHolder) convertView.getTag();
        }

        final Snap row = getItem(position);

        mImageLoader = VolleySingleton.getInstance(getContext()).getImageLoader();

        holder.iv_result_image.setImageUrl(row.getImage(), mImageLoader);
//        mImageLoader.get(row.getImage(), ImageLoader.getImageListener(holder.iv_result_image, R.drawable.rounded_corner_image_view_bg, R.drawable.rounded_corner_image_view_bg));
        holder.iv_result_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) {
                    mListener.showSnapDetails(row.getId());
                }
            }
        });
        holder.tv_snap_title.setText(row.getTitle());
        holder.tv_district.setText(row.getDistrict().getName());

        return convertView;
    }

    static class RowHolder {
        private NetworkImageView iv_result_image;
        private CustomFontTextView tv_snap_title;
        private CustomFontTextView tv_district;
    }
}
