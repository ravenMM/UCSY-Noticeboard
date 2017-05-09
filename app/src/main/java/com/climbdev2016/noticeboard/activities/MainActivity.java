package com.climbdev2016.noticeboard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.climbdev2016.noticeboard.R;
import com.climbdev2016.noticeboard.adapters.CategoryRecyclerAdapter;
import com.climbdev2016.noticeboard.adapters.StatusRecyclerAdapter;
import com.climbdev2016.noticeboard.models.CategoryModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference databaseReference;
    private List<CategoryModel> mCategoryModelList;
    private RecyclerView statusList,categorylist;
    private StatusRecyclerAdapter adapter;
    private PullRefreshLayout mPullRefreshLayout;
    private  String[] categorys = {"Tutorials","Assignments","Events","Reports","Others"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        if (mUser==null){
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            finish();
        }

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Post");
        databaseReference.keepSynced(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PostActivity.class));
            }
        });

        statusList = (RecyclerView) findViewById(R.id.status_list);
        statusList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StatusRecyclerAdapter(this, databaseReference);
        statusList.setAdapter(adapter);

        mCategoryModelList = new ArrayList<>();
        categorylist = (RecyclerView) findViewById(R.id.category_list);
        categorylist.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        CategoryRecyclerAdapter categoryRecyclerAdapter = new CategoryRecyclerAdapter(this,mCategoryModelList);
        CategoryRecyclerAdapter.OnItemClickListener itemClickListener = new CategoryRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this,CategoryActivity.class);
                intent.putExtra("category",categorys[position]);
                startActivity(intent);

            }
        };
        categoryRecyclerAdapter.setOnItemCLickListener(itemClickListener);
        categorylist.setAdapter(categoryRecyclerAdapter);


        prepareCategory();

        mPullRefreshLayout = (PullRefreshLayout) findViewById(R.id.swiperefreshlayout);

        mPullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.notifyDataSetChanged();
                statusList.setAdapter(adapter);
                mPullRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void prepareCategory() {

        CategoryModel model;
        for (int i=0;i<categorys.length;i++){
            model = new CategoryModel(categorys[i]);
            mCategoryModelList.add(model);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id==R.id.action_profile){
            goToProfile();
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToProfile() {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
    }
}
