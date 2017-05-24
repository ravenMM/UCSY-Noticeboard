package com.climbdev2016.noticeboard.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.climbdev2016.noticeboard.ui.ExpandableTextView;
import com.climbdev2016.noticeboard.R;
import com.climbdev2016.noticeboard.models.Post;
import com.climbdev2016.noticeboard.utils.Constants;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ProfileStatusRecyclerAdapter
        extends FirebaseRecyclerAdapter<Post, ProfileStatusRecyclerAdapter.StatusViewHolder> {

    private Context mContext;
    private DatabaseReference mDatabaseReference;

    public ProfileStatusRecyclerAdapter(Context context, Query ref) {
        super(Post.class, R.layout.profile_status_item, StatusViewHolder.class, ref);
        this.mContext = context;
    }

    @Override
    protected void populateViewHolder(StatusViewHolder viewHolder, Post model, final int position) {
        viewHolder.setUser_name(model.getUser_name());
        viewHolder.setUser_profile_picture(mContext, model.getUser_profile_picture());
        viewHolder.setContent(model.getContent());

        long currentTime = System.currentTimeMillis();
        long postTime = Long.parseLong(model.getTime());
        String time = (String) DateUtils.getRelativeTimeSpanString(postTime, currentTime,DateUtils.SECOND_IN_MILLIS);
        viewHolder.setTime(time);


        viewHolder.postSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPopupMenu(view,getItemCount() - (position+1));
            }
        });
    }

    private void showPopupMenu(final View view, final int position) {

        mDatabaseReference = Constants.FIREBASE_DATABASE_REFERENCE.child("Post");
        final String post_key = getRef(position).getKey();
        MenuBuilder menuBuilder = new MenuBuilder(mContext);
        MenuInflater inflater = new MenuInflater(mContext);
        inflater.inflate(R.menu.menu_setting, menuBuilder);
        MenuPopupHelper optionsMenu = new MenuPopupHelper(mContext, menuBuilder, view);
        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_edit:
                        AlertDialog.Builder ab = new AlertDialog.Builder(mContext);
                        final EditText input = new EditText(mContext);
                        LinearLayout layout = new LinearLayout(mContext);
                        layout.setOrientation(LinearLayout.VERTICAL);
                        layout.addView(input);
                        ab.setView(layout);

                        ab.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String editText = input.getText().toString();
                                mDatabaseReference.child(post_key).child("content").setValue(editText);
                            }
                        });
                        ab.setNegativeButton("Cancel",null);
                        ab.show();

                        return true;

                    case R.id.action_delete:

                        AlertDialog.Builder deleteDialog =new AlertDialog.Builder(mContext);
                        deleteDialog.setTitle("Delete");
                        deleteDialog.setMessage(R.string.post_delete_warning_txt);
                        deleteDialog.setPositiveButton(R.string.delete_txt, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mDatabaseReference.child(post_key).removeValue();
                            }
                        });

                        deleteDialog.setNegativeButton("Cancel",null);
                        deleteDialog.show();

                    default:
                        return false;
                }
            }

            @Override
            public void onMenuModeChange(MenuBuilder menu) {}
        });

        optionsMenu.show();
    }

    @Override
    public Post getItem(int position) {
        return super.getItem(getItemCount() - (position+1));
    }

    public static class StatusViewHolder extends RecyclerView.ViewHolder{

        View mView;
        ImageView postSetting;
        public StatusViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            postSetting = (ImageView) mView.findViewById(R.id.postOverFlow);
        }

        private void setUser_name(String user_name){
            TextView mUserName = (TextView) mView.findViewById(R.id.poster_name);
            mUserName.setText(user_name);
        }

        private void setUser_profile_picture(Context context, String user_profile_picture){
            ImageView mUserProfile = (ImageView) mView.findViewById(R.id.profilePic);
            Glide.with(context).load(user_profile_picture).diskCacheStrategy(DiskCacheStrategy.ALL).into(mUserProfile);
        }

        private void setContent(String content){
            ExpandableTextView mContent = (ExpandableTextView) mView.findViewById(R.id.poster_content);
            mContent.setText(content);
        }
        private void setTime(String time){
            TextView mTime = (TextView) mView.findViewById(R.id.poster_time);
            mTime.setText(time);
        }
    }
}
