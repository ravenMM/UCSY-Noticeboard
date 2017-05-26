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
import android.widget.TextView;

import com.climbdev2016.noticeboard.R;
import com.climbdev2016.noticeboard.adapters.CategoryAdapter;
import com.climbdev2016.noticeboard.adapters.StatusAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import static com.climbdev2016.noticeboard.utils.Constants.CHILD_POST;
import static com.climbdev2016.noticeboard.utils.Constants.FIREBASE_DB_REF;
import static com.climbdev2016.noticeboard.utils.Constants.MAIN_VIEW;


public class MainActivity extends AppCompatActivity
        implements CategoryAdapter.OnItemClickListener, View.OnClickListener {

    private StatusAdapter statusAdapter;
    private DatabaseReference mPostRef;

    private RecyclerView statusList;
    private LinearLayoutManager layoutManager;

    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPostRef = FIREBASE_DB_REF.child(CHILD_POST);
        mPostRef.keepSynced(true);

        // category recycler view
        RecyclerView categoryList = (RecyclerView) findViewById(R.id.category_list);
        categoryList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        CategoryAdapter categoryAdapter = new CategoryAdapter(this);
        categoryAdapter.setOnItemCLickListener(this);
        categoryList.setAdapter(categoryAdapter);

        // status recycler view
        statusList = (RecyclerView) findViewById(R.id.status_list);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        statusList.setLayoutManager(layoutManager);
        statusAdapter = new StatusAdapter(this, mPostRef, MAIN_VIEW);

        statusAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int postCount = statusAdapter.getItemCount();
                int lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (postCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    statusList.scrollToPosition(positionStart);
                }
            }
        });
        statusList.setAdapter(statusAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
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
    public void onItemClick(String category) {
        Intent intent = new Intent(MainActivity.this,CategoryActivity.class);
        intent.putExtra(getString(R.string.key_category), category);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(MainActivity.this, PostActivity.class));
    }
}
