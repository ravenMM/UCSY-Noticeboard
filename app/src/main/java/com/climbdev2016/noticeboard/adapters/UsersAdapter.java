package com.climbdev2016.noticeboard.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.mikhaellopez.circularimageview.CircularImageView;

import static com.climbdev2016.noticeboard.utils.Constants.CHILD_USER;
import static com.climbdev2016.noticeboard.utils.Constants.FIREBASE_DB_REF;

/**
 * Created by zwe on 6/1/17.
 */

public class UsersAdapter extends FirebaseRecyclerAdapter<User,UsersAdapter.ViewHolder> {

    private Context context;
    private DatabaseReference mUserRef;

   public UsersAdapter(Context context,Query ref){
       super(User.class,R.layout.users_item,ViewHolder.class,ref);
        this.context = context;
   }

    @Override
    protected void populateViewHolder(ViewHolder viewHolder, final User model, int position) {
        viewHolder.mUsersName.setText(model.getName());
        viewHolder.mUsersOccupation.setText(model.getOccupation());
        Glide.with(context).load(model.getImage()).diskCacheStrategy(DiskCacheStrategy.ALL).into(viewHolder.mUsersImage);

        viewHolder.mUsersDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUsers(model);
            }
        });
    }

    @Override
    protected User parseSnapshot(DataSnapshot snapshot) {
        User user = super.parseSnapshot(snapshot);
        if (user!=null){
            user.setUserId(snapshot.getKey());
        }
        return user;
    }

    private void deleteUsers(User model) {

        mUserRef = FIREBASE_DB_REF.child(CHILD_USER);
        mUserRef.keepSynced(true);

        final String key = model.getUserId();
        mUserRef.child(key).removeValue();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CircularImageView mUsersImage;
        private ImageView mUsersDelete;
        private TextView mUsersName;
        private TextView mUsersOccupation;

        public ViewHolder(View itemView) {
            super(itemView);
            mUsersImage = (CircularImageView) itemView.findViewById(R.id.users_image);
            mUsersDelete = (ImageView) itemView.findViewById(R.id.users_delete);
            mUsersName = (TextView) itemView.findViewById(R.id.users_name);
            mUsersOccupation = (TextView) itemView.findViewById(R.id.users_occupation);

        }
    }
}
