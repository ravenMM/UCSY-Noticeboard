package com.climbdev2016.noticeboard.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.climbdev2016.noticeboard.R;
import com.climbdev2016.noticeboard.adapters.StatusAdapter;
import com.climbdev2016.noticeboard.utils.Constants;
import com.google.firebase.database.Query;

import static com.climbdev2016.noticeboard.utils.Constants.CATEGORY_VIEW;
import static com.climbdev2016.noticeboard.utils.Constants.CHILD_POST;
import static com.climbdev2016.noticeboard.utils.Constants.SUB_CHILD_CATEGORY;

public class CategoryActivity extends AppCompatActivity {

    private StatusAdapter statusAdapter;
    private RecyclerView statusList;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        String category = getIntent().getStringExtra(getString(R.string.key_category));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(category);
        }

        Query selectCategoryQuery = Constants.FIREBASE_DB_REF.child(CHILD_POST)
                .orderByChild(SUB_CHILD_CATEGORY).equalTo(category);

        statusList = (RecyclerView) findViewById(R.id.status_list_by_category);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        statusList.setLayoutManager(layoutManager);
        statusAdapter = new StatusAdapter(this, selectCategoryQuery, CATEGORY_VIEW);

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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            onBackPressed();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
