package com.climbdev2016.noticeboard.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.climbdev2016.noticeboard.R;
import com.climbdev2016.noticeboard.models.Post;
import com.climbdev2016.noticeboard.ui.ExpandableTextView;
import com.climbdev2016.noticeboard.utils.Constants;
import com.climbdev2016.noticeboard.utils.Utils;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;

import static com.climbdev2016.noticeboard.utils.Constants.CATEGORY_VIEW;
import static com.climbdev2016.noticeboard.utils.Constants.CHILD_LINK;
import static com.climbdev2016.noticeboard.utils.Constants.CHILD_POST;
import static com.climbdev2016.noticeboard.utils.Constants.FIREBASE_DB_REF;
import static com.climbdev2016.noticeboard.utils.Constants.PROFILE_VIEW;
import static com.climbdev2016.noticeboard.utils.Constants.SUB_CHILD_LINK;

public class StatusAdapter extends FirebaseRecyclerAdapter<Post, StatusAdapter.StatusViewHolder> {

    private Context mContext;
    private int viewCode;

    public StatusAdapter(Context context, Query ref, int viewCode) {
        super(Post.class, R.layout.status_item, StatusViewHolder.class, ref);
        this.viewCode =  viewCode;
        this.mContext = context;
    }

    @Override
    protected void populateViewHolder(final StatusViewHolder viewHolder, final Post model, final int position) {
        String time = (String) DateUtils.getRelativeTimeSpanString(
                Long.parseLong(model.getTime()),
                System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS);

        viewHolder.mUserName.setText(model.getUser_name());
        viewHolder.mContent.setText(model.getContent());
        if (model.getUser_profile_picture() == null) {
            viewHolder.mUserProfile.setImageDrawable(ContextCompat.getDrawable(mContext,
                    R.drawable.ic_account_circle_black_24dp));
        } else {
            Glide.with(mContext).load(model.getUser_profile_picture())
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(viewHolder.mUserProfile);
        }
        viewHolder.mTime.setText(time);

        if (viewCode == CATEGORY_VIEW) {
            viewHolder.hideCategory();
        } else {
            viewHolder.showCategory();
            viewHolder.mCategory.setText(model.getCategory());
        }

        viewHolder.mOverFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUpMenu(view, model);
            }
        });
    }

    @Override
    protected Post parseSnapshot(DataSnapshot snapshot) {
        Post post = super.parseSnapshot(snapshot);
        if (post != null) {
            post.setPost_id(snapshot.getKey());
        }
        return post;
    }

    public void showPopUpMenu(View view, final Post model) {
        final String key = model.getPost_id();
        final DatabaseReference postRef = FIREBASE_DB_REF.child(CHILD_POST);
        MenuBuilder menuBuilder = new MenuBuilder(mContext);
        MenuInflater inflater = new MenuInflater(mContext);
        MenuPopupHelper optionsMenu = new MenuPopupHelper(mContext, menuBuilder, view);
        if (viewCode == PROFILE_VIEW) {
            inflater.inflate(R.menu.menu_setting, menuBuilder);
        } else {
            inflater.inflate(R.menu.menu_share, menuBuilder);
            optionsMenu.setForceShowIcon(true);
        }

        MenuBuilder.Callback callback = new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_share:
                        if (!Utils.isOnline(mContext)) {
                            Toasty.error(mContext, "You are offline!", Toast.LENGTH_SHORT, true).show();
                            return false;
                        } else {
                            final ShareHashtag shareHashtag = new ShareHashtag.Builder().setHashtag(Constants.SHARE_HASH_TAG).build();
                            final ShareDialog shareDialog = new ShareDialog((Activity) mContext);
                            if (ShareDialog.canShow(ShareLinkContent.class)) {
                                DatabaseReference mLinkRef = FIREBASE_DB_REF.child(CHILD_LINK);
                                mLinkRef.child(SUB_CHILD_LINK).addValueEventListener(new ValueEventListener() {
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
                        }
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
                                postRef.child(key).child("content").setValue(editText);
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
                                postRef.child(key).removeValue();
                            }
                        });

                        deleteDialog.setNegativeButton("Cancel",null);
                        deleteDialog.show();
                        return true;

                    default:
                        return false;
                }
            }
            @Override
            public void onMenuModeChange(MenuBuilder menu) {

            }
        };
        menuBuilder.setCallback(callback);
        optionsMenu.show();
    }

    public static class StatusViewHolder extends RecyclerView.ViewHolder{

        private View line;
        private TextView postIn;
        private TextView mCategory;
        private TextView mUserName;
        private TextView mTime;
        private ImageView mUserProfile;
        private ImageView mOverFlow;
        private ExpandableTextView mContent;

        public StatusViewHolder(View itemView) {
            super(itemView);
            mUserName = (TextView) itemView.findViewById(R.id.name);
            mCategory = (TextView) itemView.findViewById(R.id.category);
            postIn = (TextView) itemView.findViewById(R.id.postIn);
            mTime = (TextView) itemView.findViewById(R.id.time);
            mUserProfile = (ImageView) itemView.findViewById(R.id.profilePic);
            mOverFlow = (ImageView) itemView.findViewById(R.id.mainOverFlow);
            mContent = (ExpandableTextView) itemView.findViewById(R.id.content);
            line = itemView.findViewById(R.id.line_view);
        }

        private void showCategory() {
            postIn.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
            mCategory.setVisibility(View.VISIBLE);
        }

        private void hideCategory() {
            postIn.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
            mCategory.setVisibility(View.GONE);
        }
    }
}
