package com.climbdev2016.noticeboard.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.climbdev2016.noticeboard.R;
import com.climbdev2016.noticeboard.adapters.SelectCategoryAdapter;
import com.climbdev2016.noticeboard.utils.Constants;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class CategoryActivity extends AppCompatActivity {

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

        Query selectCategoryQuery = Constants.FIREBASE_DATABASE_REFERENCE.child(getString(R.string.child_post))
                .orderByChild(getString(R.string.child_post_category)).equalTo(category);

        RecyclerView selectCategoryRecycler = (RecyclerView) findViewById(R.id.status_list_by_category);
        selectCategoryRecycler.setLayoutManager(new LinearLayoutManager(this));
        selectCategoryRecycler.setAdapter(new SelectCategoryAdapter(this, selectCategoryQuery));
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
