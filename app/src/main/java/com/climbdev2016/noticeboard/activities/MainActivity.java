package com.climbdev2016.noticeboard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.baoyz.widget.PullRefreshLayout;
import com.climbdev2016.noticeboard.R;
import com.climbdev2016.noticeboard.adapters.CategoryAdapter;
import com.climbdev2016.noticeboard.adapters.StatusAdapter;
import com.climbdev2016.noticeboard.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity
        implements PullRefreshLayout.OnRefreshListener,
        CategoryAdapter.OnItemClickListener, View.OnClickListener {

    private StatusAdapter adapter;
    private PullRefreshLayout pullRefreshLayout;
    private DatabaseReference mPostRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPostRef = Constants.FIREBASE_DATABASE_REFERENCE.child(getString(R.string.child_post));
        mPostRef.keepSynced(true);

        RecyclerView categoryList = (RecyclerView) findViewById(R.id.category_list);
        categoryList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        CategoryAdapter categoryAdapter =
                new CategoryAdapter(getResources().getStringArray(R.array.post_categories));
        categoryAdapter.setOnItemCLickListener(this);
        categoryList.setAdapter(categoryAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        pullRefreshLayout = (PullRefreshLayout) findViewById(R.id.pull_refresh_main);
        pullRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_profile){
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        adapter.notifyDataSetChanged();
        pullRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemClick(String category) {
        Intent intent = new Intent(MainActivity.this,CategoryActivity.class);
        intent.putExtra(getString(R.string.key_category), category);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(MainActivity.this, PostActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        RecyclerView statusList = (RecyclerView) findViewById(R.id.status_list);
        statusList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StatusAdapter(this, mPostRef);
        statusList.setAdapter(adapter);
    }
}
