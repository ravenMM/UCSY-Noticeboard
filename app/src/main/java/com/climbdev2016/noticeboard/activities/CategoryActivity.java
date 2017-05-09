package com.climbdev2016.noticeboard.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.climbdev2016.noticeboard.R;
import com.climbdev2016.noticeboard.adapters.SelectCategoryAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class CategoryActivity extends AppCompatActivity {

    private Query selectCategoryQuery;
    private RecyclerView selectCategoryRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        String category = getIntent().getStringExtra("category");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(category);

        selectCategoryQuery = FirebaseDatabase.getInstance().getReference().child("Post")
                .orderByChild("category").equalTo(category);

        selectCategoryRecycler = (RecyclerView) findViewById(R.id.select_category_recycler);
        selectCategoryRecycler.setLayoutManager(new LinearLayoutManager(this));
        selectCategoryRecycler.setAdapter(new SelectCategoryAdapter(this,selectCategoryQuery));

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
