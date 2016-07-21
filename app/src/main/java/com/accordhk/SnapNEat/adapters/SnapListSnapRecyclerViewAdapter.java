package com.accordhk.SnapNEat.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.models.Snap;
import com.accordhk.SnapNEat.utils.VolleySingleton;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

/**
 * Created by jm on 22/2/16.
 */
public class SnapListSnapRecyclerViewAdapter extends RecyclerView.Adapter<SnapListSnapRecyclerViewAdapter.ViewHolder> {

    private List<Snap> mData;
    ImageLoader mImageLoader;
    Context mContext;
    int mResource;

    private SnapListSnapRecyclerViewAdapterListener mListener;

    public interface SnapListSnapRecyclerViewAdapterListener {
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

    public SnapListSnapRecyclerViewAdapter(Context c, List<Snap> mData, int resource, SnapListSnapRecyclerViewAdapterListener l) {
        this.mData = mData;
        this.mContext = c;
        this.mResource = resource;
        mListener = l;
        mImageLoader = VolleySingleton.getInstance(mContext).getImageLoader();
    }

    @Override
    public SnapListSnapRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(mResource, parent, false);
        view.setPadding(5, 0, 5, 0);
        view.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.transparent));
        // set the view's size, margins, paddings and layout PARAMETERS

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(SnapListSnapRecyclerViewAdapter.ViewHolder holder, final int position) {

        holder.iv_result_image.setImageUrl(mData.get(position).getImageThumbnail(), mImageLoader);
//        mImageLoader.get(mData.get(position).getImageThumbnail(), ImageLoader.getImageListener(holder.iv_result_image, R.drawable.rounded_corner_image_view_bg, R.drawable.rounded_corner_image_view_bg));
        holder.iv_result_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) {
                    //mListener.showSnapDetails(Integer.parseInt(mData.get(position).getnId()));
                    if(mData.get(position).getId() > 0)
                        mListener.showSnapDetails(mData.get(position).getId());
                    else if (Integer.parseInt(mData.get(position).getnId()) > 0)
                        mListener.showSnapDetails(Integer.parseInt(mData.get(position).getnId()));

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
