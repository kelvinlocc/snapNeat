package com.accordhk.SnapNEat.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.accordhk.SnapNEat.R;
import com.accordhk.SnapNEat.models.User;
import com.accordhk.SnapNEat.utils.Constants;
import com.accordhk.SnapNEat.utils.SharedPref;
import com.accordhk.SnapNEat.utils.VolleySingleton;
import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jm on 3/2/16.
 */
public class SearchResultByUserAdapter extends ArrayAdapter<User> {

    private static String LOGGER_TAG = "SearchResultByUserAdapter";

    private SearchResultByUserAdapterInterface mListener;

    ImageLoader mImageLoader;

    List<User> users;

    private View v;

    User currentUser;

    public interface SearchResultByUserAdapterInterface {
        void showUserProfile(int userId);
        void showSnapDetails(int id);
        void followUser(int userId, View view);
    }

    public SearchResultByUserAdapter(Context context, int resource, List<User> objects, SearchResultByUserAdapterInterface l) {
        super(context, resource, objects);
        mListener = l;
        users = objects;
        currentUser = new SharedPref(getContext()).getLoggedInUser();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_search_result_by_user_row, parent, false);
        }

        v = convertView;

        User row = getItem(position);

        CircleImageView profile_pic = (CircleImageView) convertView.findViewById(R.id.profile_pic);
        TextView username = (TextView) convertView.findViewById(R.id.username);
        RecyclerView gv_search_results_user_row = (RecyclerView) convertView.findViewById(R.id.gv_search_results_user_row);

        ImageButton btn_add_user = (ImageButton) convertView.findViewById(R.id.btn_add_user);

        if(row.getFollowFlag() == Constants.FLAG_TRUE || (currentUser != null && row.getId() == currentUser.getId())) {
           btn_add_user.setVisibility(View.INVISIBLE);
        } else {
            btn_add_user.setVisibility(View.VISIBLE);
            btn_add_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null)
                        mListener.followUser(getItem(position).getId(), v);
                }
            });
        }

        mImageLoader = VolleySingleton.getInstance(getContext()).getImageLoader();

        mImageLoader.get(row.getAvatarThumbnail(), ImageLoader.getImageListener(profile_pic, R.drawable.s1_bg_profile_pic, R.drawable.s1_bg_profile_pic));
        username.setText(row.getUsername());

//        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
//        int h = (int)Math.ceil(row.getSnaps().size()/3);
//        float mod = row.getSnaps().size()%3;
//        if(mod == 1)
//            h++;
//
//        if(row.getSnaps().size() > 0)
//            gv_search_results_user_row.getLayoutParams().height = ((int) h * (metrics.widthPixels / 3)) + 10; // 10 is for the margin
//        gv_search_results_user_row.setAdapter(new SearchResultByUserRowAdapter(getContext(), R.layout.list_search_result_by_user_row_item, row.getSnaps()));
//
//        gv_search_results_user_row.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if(mListener != null) {
//                    mListener.showSnapDetails((int) view.getTag());
//                }
//            }
//        });

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) {
                    mListener.showUserProfile(getItem(position).getId());
                }
            }
        });

        SnapListSnapRecyclerViewAdapter adapter = new SnapListSnapRecyclerViewAdapter(getContext(), row.getSnaps(), R.layout.list_gv_snap_item,
                new SnapListSnapRecyclerViewAdapter.SnapListSnapRecyclerViewAdapterListener() {
                    @Override
                    public void showSnapDetails(int snapId) {
                        if (mListener != null)
                            mListener.showSnapDetails(snapId);
                    }
                });
        gv_search_results_user_row.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        gv_search_results_user_row.setAdapter(adapter);

        return convertView;
    }
}
