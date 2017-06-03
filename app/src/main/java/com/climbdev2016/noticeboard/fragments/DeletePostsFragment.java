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
import com.google.firebase.database.Query;

import static com.climbdev2016.noticeboard.utils.Constants.APPROVE_NO;
import static com.climbdev2016.noticeboard.utils.Constants.APPROVE_YES;
import static com.climbdev2016.noticeboard.utils.Constants.CHILD_POST;
import static com.climbdev2016.noticeboard.utils.Constants.DELETE_POST_VIEW;
import static com.climbdev2016.noticeboard.utils.Constants.FIREBASE_DB_REF;
import static com.climbdev2016.noticeboard.utils.Constants.PENDING_VIEW;
import static com.climbdev2016.noticeboard.utils.Constants.SUB_CHILD_POST_APPROVE;

/**
 * Created by zwe on 6/1/17.
 */

public class DeletePostsFragment extends Fragment {
    private Context mContext;
    private LinearLayoutManager layoutManager;
    private RecyclerView deletepostlist;
    private StatusAdapter statusAdapter;
    private Query currentPending;

    public DeletePostsFragment(){

    }

    public DeletePostsFragment(Context context){
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_posts,container,false);

        currentPending = FIREBASE_DB_REF.child(CHILD_POST).orderByChild(SUB_CHILD_POST_APPROVE).equalTo(APPROVE_YES);
        currentPending.keepSynced(true);

        deletepostlist = (RecyclerView) view.findViewById(R.id.delete_posts_list);
        layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        deletepostlist.setLayoutManager(layoutManager);
        statusAdapter = new StatusAdapter(getContext(),currentPending,DELETE_POST_VIEW);
        deletepostlist.setAdapter(statusAdapter);

        return view;
    }
}
