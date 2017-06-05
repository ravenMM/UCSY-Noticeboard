package com.climbdev2016.noticeboard.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.climbdev2016.noticeboard.R;
import com.climbdev2016.noticeboard.adapters.StatusAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.climbdev2016.noticeboard.utils.Constants.APPROVE_NO;
import static com.climbdev2016.noticeboard.utils.Constants.CHILD_POST;
import static com.climbdev2016.noticeboard.utils.Constants.FIREBASE_DB_REF;
import static com.climbdev2016.noticeboard.utils.Constants.MAIN_VIEW;
import static com.climbdev2016.noticeboard.utils.Constants.PENDING_VIEW;
import static com.climbdev2016.noticeboard.utils.Constants.SUB_CHILD_POST_APPROVE;

/**
 * Created by zwe on 5/31/17.
 */

public class PendingFragment extends Fragment{
    private Context mContext;
    private LinearLayoutManager layoutManager;
    private RecyclerView pendinglist;
    private StatusAdapter statusAdapter;
    private Query currentPending;

    public PendingFragment(){

    }
    public PendingFragment(Context context){
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View  view = inflater.inflate(R.layout.fragment_pending,container,false);

        currentPending = FIREBASE_DB_REF.child(CHILD_POST).orderByChild(SUB_CHILD_POST_APPROVE).equalTo(APPROVE_NO);
        currentPending.keepSynced(true);

        pendinglist = (RecyclerView) view.findViewById(R.id.pending_list);
        layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        pendinglist.setLayoutManager(layoutManager);
        statusAdapter = new StatusAdapter(getContext(),currentPending,PENDING_VIEW);
        statusAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                int postCount = statusAdapter.getItemCount();
                int lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (postCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    pendinglist.scrollToPosition(positionStart);
                }
            }
        });

        pendinglist.setAdapter(statusAdapter);

        emptyView(view);
        return view;
    }

    private void emptyView(final View view) {
        currentPending.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    view.findViewById(R.id.nopending).setVisibility(View.GONE);
                }else {
                    view.findViewById(R.id.nopending).setVisibility(View.VISIBLE);
                    pendinglist.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
