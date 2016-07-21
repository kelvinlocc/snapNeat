package com.accordhk.SnapNEat.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.utils.LocalImageCache;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.util.List;

/**
 * Created by jm on 22/2/16.
 */
public class PostNewSnapRecyclerViewAdapter extends RecyclerView.Adapter<PostNewSnapRecyclerViewAdapter.ViewHolder> {

    private List<String> mData;
    ImageLoader mImageLoader;
    Context mContext;
    int mResource;

    private PostNewSnapRecyclerViewAdapterListener mListener;

    public interface PostNewSnapRecyclerViewAdapterListener {
        void removeBitmap(String bitmap);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public NetworkImageView iv_result_image;
        public ImageButton btn_delete;
        public ViewHolder(View v) {
            super(v);
            iv_result_image = (NetworkImageView) v.findViewById(R.id.iv_result_image);
            btn_delete = (ImageButton) v.findViewById(R.id.btn_delete);
        }
    }

    public PostNewSnapRecyclerViewAdapter(Context c, List<String> mData, int resource, PostNewSnapRecyclerViewAdapterListener l) {
        this.mData = mData;
        this.mContext = c;
        this.mResource = resource;
        mListener = l;
        mImageLoader = new ImageLoader(Volley.newRequestQueue(c), new LocalImageCache());
    }

    public void swap(List<String> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public PostNewSnapRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(PostNewSnapRecyclerViewAdapter.ViewHolder holder, final int position) {

//        holder.iv_result_image.setImageBitmap(mData.get(position));
//        holder.iv_result_image.setImageURI(Uri.parse(mData.get(position)));
        Log.d("PostNewSnapRecyclerr", mData.get(position));
        holder.iv_result_image.setImageUrl(mData.get(position), mImageLoader);
        //mImageLoader.get(mData.get(position), ImageLoader.getImageListener(holder.iv_result_image, R.drawable.rounded_corner_image_view_bg, R.drawable.rounded_corner_image_view_bg));
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null)
                    mListener.removeBitmap(mData.get(position));

            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
