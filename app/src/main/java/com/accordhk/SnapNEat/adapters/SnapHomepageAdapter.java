package com.accordhk.SnapNEat.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.accordhk.SnapNEat.models.Snap;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.utils.Constants;
import com.accordhk.SnapNEat.utils.CustomFontTextView;
import com.accordhk.SnapNEat.utils.VolleySingleton;
import com.accordhk.SnapNEat.utils.mySharePreference_app;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

/**
 * Created by jm on 7/2/16.
 */
public class SnapHomepageAdapter extends ArrayAdapter<Snap> {

    private static String LOGGER_TAG = "SnapHomepageAdapter";
    String TAG = LOGGER_TAG;
    ImageLoader mImageLoader;
    private Context mContext;
    mySharePreference_app myPref;
    private SnapCardListener mListener;

    public interface SnapCardListener {
        public void onProfilePicClick(View v, Snap snap);

        public void onLikeClick(View v, Snap snap);

        public void onShareClick(View v, Snap snap);

        public void onNavigateClick(View v, Snap snap);
    }

    public SnapHomepageAdapter(Context context, int resource, List<Snap> objects, SnapCardListener listener) {
        super(context, resource, objects);
        mContext = context;
        mListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        myPref = new mySharePreference_app();
        final Snap snap = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card_stack_item, parent, false);
        }

        CustomFontTextView food_name = (CustomFontTextView) convertView.findViewById(R.id.food_name);
        food_name.setText(snap.getTitle());

        CustomFontTextView district = (CustomFontTextView) convertView.findViewById(R.id.district);
        district.setText(snap.getDistrict().getName());

        CustomFontTextView username = (CustomFontTextView) convertView.findViewById(R.id.username);
        username.setText(String.valueOf(snap.getUser().getUsername()));

        CustomFontTextView like_count = (CustomFontTextView) convertView.findViewById(R.id.like_count);
        like_count.setText(String.valueOf(snap.getTotalLikes()));

        mImageLoader = VolleySingleton.getInstance(mContext).getImageLoader();

        ImageView profile_pic = (ImageView) convertView.findViewById(R.id.profile_pic);
        mImageLoader.get(snap.getUser().getAvatarThumbnail(), ImageLoader.getImageListener(profile_pic,
                R.drawable.s1_bg_profile_pic, R.drawable.s1_bg_profile_pic));
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onProfilePicClick(v, snap);
            }
        });

        ImageButton btn_navigate = (ImageButton) convertView.findViewById(R.id.btn_navigate);
        btn_navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onNavigateClick(v, snap);
            }
        });

        RelativeLayout rl_location = (RelativeLayout) convertView.findViewById(R.id.rl_location);
        rl_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onNavigateClick(v, snap);
            }
        });

        ImageButton btn_share = (ImageButton) convertView.findViewById(R.id.btn_share);
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onShareClick(v, snap);
            }
        });

        ImageButton btn_like = (ImageButton) convertView.findViewById(R.id.btn_like);
        btn_like.setTag(convertView);
        btn_like.setImageResource(R.drawable.s1_btn_like_default);

        if (snap.getLikeFlag() == Constants.FLAG_TRUE) {
            btn_like.setImageResource(R.drawable.s1_btn_like);
        }
        else {
        }
        Log.i(TAG, "check@ getView: snap.getLikeFlag(): "+snap.getLikeFlag());
        Log.i(TAG, "getTitle: "+snap.getTitle());
        Log.i(TAG, "getUsername: "+snap.getUser().getUsername());
        Log.i(TAG, "getTotalLikes: "+snap.getTotalLikes());


        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLikeClick(v, snap);
            }
        });

        RelativeLayout rl_like = (RelativeLayout) convertView.findViewById(R.id.rl_like);
        rl_like.setTag(convertView);
        rl_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLikeClick(v, snap);
            }
        });

        NetworkImageView snap_photo = (NetworkImageView) convertView.findViewById(R.id.snap_photo);
        snap_photo.setImageUrl(snap.getImage(), mImageLoader);
//        mImageLoader.get(snap.getImage(), ImageLoader.getImageListener(snap_photo, R.drawable.rounded_corner_image_view_bg, R.drawable.rounded_corner_image_view_bg));
//        snap_photo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mListener.onSnapPhotoClick(v, snap);
//            }
//        });

        ImageView rating = (ImageView) convertView.findViewById(R.id.rating);
        //int id = mContext.getResources().getIdentifier("com.accordhkdev.snapneat:drawable/s1_rating_" + snap.getRating(), null, null);
        int id = mContext.getResources().getIdentifier("s1_rating_" + snap.getRating(), "drawable", mContext.getPackageName());
        rating.setImageResource(id);

        return convertView;
        //return super.getView(position, convertView, parent);
    }

    public void directLike (){

    }
}
