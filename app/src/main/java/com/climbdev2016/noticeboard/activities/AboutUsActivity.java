package com.climbdev2016.noticeboard.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.climbdev2016.noticeboard.R;
import com.climbdev2016.noticeboard.adapters.TeamAdapter;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import static com.climbdev2016.noticeboard.utils.Constants.CHILD_ADMIN;
import static com.climbdev2016.noticeboard.utils.Constants.FIREBASE_DB_REF;

public class AboutUsActivity extends AppCompatActivity {

    private LinearLayoutManager manager;
    private RecyclerView mTeamRecyclerView;
    private DatabaseReference mAdminRef;
    private TeamAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        mAdminRef = FIREBASE_DB_REF.child(CHILD_ADMIN);
        mTeamRecyclerView = (RecyclerView) findViewById(R.id.teamList);

        manager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mTeamRecyclerView.setLayoutManager(manager);

        adapter = new TeamAdapter(this,mAdminRef);
        mTeamRecyclerView.setAdapter(adapter);

    }



}
