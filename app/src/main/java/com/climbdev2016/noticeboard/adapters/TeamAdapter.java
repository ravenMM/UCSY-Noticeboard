package com.climbdev2016.noticeboard.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.climbdev2016.noticeboard.R;
import com.climbdev2016.noticeboard.models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;

/**
 * Created by zwe on 6/3/17.
 */

public class TeamAdapter extends FirebaseRecyclerAdapter<User,TeamAdapter.ViewHolder> {


    private Context mContext;

    public TeamAdapter(Context context, Query ref) {
        super(User.class, R.layout.team_item, ViewHolder.class, ref);
        this.mContext = context;
    }

    @Override
    protected void populateViewHolder(TeamAdapter.ViewHolder viewHolder, User model, int position) {
        viewHolder.name.setText(model.getName());
        viewHolder.type.setText(model.getOccupation());
        Glide.with(mContext).load(model.getImage()).diskCacheStrategy(DiskCacheStrategy.ALL).into(viewHolder.image);
    }

    @Override
    protected User parseSnapshot(DataSnapshot snapshot) {

        User user = super.parseSnapshot(snapshot);
        if (user!=null){
            user.setUserId(snapshot.getKey());
        }
        return user;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView name;
        private TextView type;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.teamProfile);
            name = (TextView) itemView.findViewById(R.id.teammembername);
            type = (TextView) itemView.findViewById(R.id.teammembertype);
        }
    }
}
