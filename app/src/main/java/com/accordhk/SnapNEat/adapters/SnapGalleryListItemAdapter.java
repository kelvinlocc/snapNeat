package com.accordhk.SnapNEat.adapters;

import android.content.Context;
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
 * Created by jm on 3/2/16.
 */
public class SnapGalleryListItemAdapter extends ArrayAdapter<Snap> {

    private static String LOGGER_TAG = "SnapGalleryListItemAdapter";

    ImageLoader mImageLoader;

    private SnapGalleryListItemAdapterListener mListener;

    public interface SnapGalleryListItemAdapterListener {
        void showSnapDetails(int snapId);
    }

    public SnapGalleryListItemAdapter(Context context, int resource, List<Snap> objects, SnapGalleryListItemAdapterListener l) {
        super(context, resource, objects);
        mListener = l;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RowHolder holder;

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_gv_snap_item, parent, false);

            holder = new RowHolder();
            holder.iv_result_image = (NetworkImageView) convertView.findViewById(R.id.iv_result_image);

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

        return convertView;
    }

    static class RowHolder {
        private NetworkImageView iv_result_image;
    }
}
