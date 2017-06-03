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
import android.widget.LinearLayout;

import com.climbdev2016.noticeboard.R;
import com.climbdev2016.noticeboard.adapters.UsersAdapter;
import com.google.firebase.database.DatabaseReference;

import static com.climbdev2016.noticeboard.utils.Constants.CHILD_USER;
import static com.climbdev2016.noticeboard.utils.Constants.FIREBASE_DB_REF;

/**
 * Created by zwe on 6/1/17.
 */

public class UsersFragment extends Fragment {

    private Context context;
    private DatabaseReference mUsersRef;
    private RecyclerView mUserList;
    private LinearLayoutManager layoutManager;
    private UsersAdapter usersAdapter;

    public UsersFragment(){

    }

    public UsersFragment(Context context){
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        mUsersRef = FIREBASE_DB_REF.child(CHILD_USER);
        mUsersRef.keepSynced(true);

        mUserList = (RecyclerView) view.findViewById(R.id.users_list);
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        mUserList.setLayoutManager(layoutManager);
        usersAdapter = new UsersAdapter(context,mUsersRef);
        usersAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                int postCount = usersAdapter.getItemCount();
                int lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (postCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    mUserList.scrollToPosition(positionStart);
                }
            }
        });
        mUserList.setAdapter(usersAdapter);

        return view;
    }
}
