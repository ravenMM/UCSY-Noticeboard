package com.climbdev2016.noticeboard.adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.climbdev2016.noticeboard.R;
import com.climbdev2016.noticeboard.models.NoticeboardModel;
import com.climbdev2016.noticeboard.ui.ExpandableTextView;
import com.climbdev2016.noticeboard.utils.Constants;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class StatusAdapter extends FirebaseRecyclerAdapter<NoticeboardModel, StatusAdapter.StatusViewHolder> {

    private Context mContext;
    private DatabaseReference mLinkRef;

    public StatusAdapter(Context context, Query ref) {
        super(NoticeboardModel.class, R.layout.main_status_item, StatusViewHolder.class, ref);
        this.mContext = context;
    }

    @Override
    protected void populateViewHolder(final StatusViewHolder viewHolder, final NoticeboardModel model, int position) {

        viewHolder.setUser_name(model.getUser_name());
        viewHolder.setContent(model.getContent());
        viewHolder.setUser_profile_picture(mContext,model.getUser_profile_picture());
        long currentTime = System.currentTimeMillis();
        long postTime = -1 * Long.parseLong(model.getTime());

        String time = (String) DateUtils.getRelativeTimeSpanString(postTime, currentTime,DateUtils.SECOND_IN_MILLIS);
        viewHolder.setTime(time);

        viewHolder.mOverFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPopUpMenu(view,model);

            }
        });
        viewHolder.setCategory(model.getCategory());

    }

    private void showPopUpMenu(View view, final NoticeboardModel model) {

        mLinkRef = FirebaseDatabase.getInstance().getReference().child(mContext.getString(R.string.child_link));

        MenuBuilder menuBuilder = new MenuBuilder(mContext);
        MenuInflater inflater = new MenuInflater(mContext);
        inflater.inflate(R.menu.menu_share, menuBuilder);
        MenuPopupHelper optionsMenu = new MenuPopupHelper(mContext, menuBuilder, view);
        optionsMenu.setForceShowIcon(true);

        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_share:
                        final ShareHashtag shareHashtag = new ShareHashtag.Builder().setHashtag(Constants.SHARE_HASH_TAG).build();
                        final ShareDialog shareDialog = new ShareDialog((Activity) mContext);
                        if (ShareDialog.canShow(ShareLinkContent.class)) {

                            mLinkRef.child(mContext.getString(R.string.child_link_link)).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String appLink = (String) dataSnapshot.getValue();

                                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                            .setQuote(model.getContent()+"\n.... Download here \uD83D\uDC47 \uD83D\uDC47 ....")
                                            .setContentUrl(Uri.parse(appLink))
                                            .setShareHashtag(shareHashtag)
                                            .build();
                                    shareDialog.show(linkContent);

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                        return true;

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
    public NoticeboardModel getItem(int position) {
        return super.getItem(getItemCount() - (position+1));
    }



    static class StatusViewHolder extends RecyclerView.ViewHolder{

        View mView;
        private ImageView mOverFlow;
        public StatusViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mOverFlow = (ImageView) mView.findViewById(R.id.mainOverFlow);

        }

        private void setUser_name(String user_name){
            TextView mUserName = (TextView) mView.findViewById(R.id.name);
            mUserName.setText(user_name);
        }

        private void setUser_profile_picture(Context context, String user_profile_picture){
            ImageView mUserProfile = (ImageView) mView.findViewById(R.id.profilePic);
            Glide.with(context).load(user_profile_picture).diskCacheStrategy(DiskCacheStrategy.ALL).into(mUserProfile);
        }

        private void setContent(String content){
            ExpandableTextView mContent = (ExpandableTextView) mView.findViewById(R.id.content);
            mContent.setText(content);
        }
        private void setTime(String time){
            TextView mTime = (TextView) mView.findViewById(R.id.time);
            mTime.setText(time);
        }
        private void setCategory(String category){
            TextView mCategory = (TextView) mView.findViewById(R.id.category);
            mCategory.setText(category);
        }
    }
}
