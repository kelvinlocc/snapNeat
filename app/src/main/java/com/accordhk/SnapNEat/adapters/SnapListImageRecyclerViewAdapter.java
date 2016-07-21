package com.accordhk.SnapNEat.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.utils.VolleySingleton;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

/**
 * Created by jm on 22/2/16.
 */
public class SnapListImageRecyclerViewAdapter extends RecyclerView.Adapter<SnapListImageRecyclerViewAdapter.ViewHolder> {

    private List<String> mData;
    ImageLoader mImageLoader;
    Context mContext;
    int mResource;

    int width;
    int height;

    private SnapListImageRecyclerViewAdapterListener mListener;

    public interface SnapListImageRecyclerViewAdapterListener {
        void showSnapDetails(int snapId);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public NetworkImageView iv_result_image;
        public ViewHolder(View v) {
            super(v);
            iv_result_image = (NetworkImageView) v.findViewById(R.id.iv_result_image);
        }
    }

    public SnapListImageRecyclerViewAdapter(Context c, List<String> mData, int resource, int w, int h, SnapListImageRecyclerViewAdapterListener l) {
        this.mData = mData;
        this.mContext = c;
        this.mResource = resource;
        mListener = l;
        width = w;
        height = h;
        mImageLoader = VolleySingleton.getInstance(mContext).getImageLoader();
    }

    @Override
    public SnapListImageRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(mResource, parent, false);
        view.setPadding(5, 0, 5, 0);
        view.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.transparent));
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(SnapListImageRecyclerViewAdapter.ViewHolder holder, final int position) {

        holder.iv_result_image.getLayoutParams().height = height;
        holder.iv_result_image.getLayoutParams().width = width;
        holder.iv_result_image.setImageUrl(mData.get(position).toString(), mImageLoader);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
