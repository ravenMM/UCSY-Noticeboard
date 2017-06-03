package com.climbdev2016.noticeboard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.climbdev2016.noticeboard.R;
import com.climbdev2016.noticeboard.adapters.CategoryAdapter;
import com.climbdev2016.noticeboard.adapters.StatusAdapter;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.climbdev2016.noticeboard.utils.Constants.APPROVE_YES;
import static com.climbdev2016.noticeboard.utils.Constants.CHILD_ADMIN;
import static com.climbdev2016.noticeboard.utils.Constants.CHILD_POST;
import static com.climbdev2016.noticeboard.utils.Constants.CHILD_USER;
import static com.climbdev2016.noticeboard.utils.Constants.FIREBASE_DB_REF;
import static com.climbdev2016.noticeboard.utils.Constants.MAIN_VIEW;
import static com.climbdev2016.noticeboard.utils.Constants.SUB_CHILD_POST_APPROVE;


public class MainActivity extends AppCompatActivity
        implements CategoryAdapter.OnItemClickListener, View.OnClickListener {

    private StatusAdapter statusAdapter;
    private DatabaseReference mPostRef;
    private DatabaseReference mUserRef;
    private DatabaseReference mAdminRef;
    private RecyclerView statusList;
    private LinearLayoutManager layoutManager;
    private AdView mAdView;
    private FloatingActionButton fab;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private Query currentApprove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        currentApprove =FIREBASE_DB_REF.child(CHILD_POST)
                .orderByChild(SUB_CHILD_POST_APPROVE).equalTo(APPROVE_YES);
        currentApprove.keepSynced(true);
        //Admob
        MobileAds.initialize(this, getString(R.string.ad_app_id));

        mAdView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        try{
            findViewById(R.id.action_admin).setVisibility(View.INVISIBLE);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

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
        statusAdapter = new StatusAdapter(this, currentApprove, MAIN_VIEW);

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

        fab = (FloatingActionButton) findViewById(R.id.fab);
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
        }else if (id==R.id.action_logout){
            signOut();
        }else if (id==R.id.action_admin){
            startActivity(new Intent(MainActivity.this, AdminActivity.class));
            return true;
        }else if (id==R.id.action_about){
            startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mUserRef = FIREBASE_DB_REF.child(CHILD_USER);
        mUserRef.keepSynced(true);

        if (mUser == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }else {

            final String user = mUser.getUid();
            mUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(user)){
                        try{
                            fab.setVisibility(View.GONE);
                            findViewById(R.id.action_profile).setVisibility(View.GONE);
                            findViewById(R.id.action_admin).setVisibility(View.GONE);
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }

                    }else {
                        checkAdmin();
                        mAdView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }

    private void checkAdmin() {
        mAdminRef = FIREBASE_DB_REF.child(CHILD_ADMIN);
        mAdminRef.keepSynced(true);

        final String user = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mAdminRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(user)){
                    try{
                        findViewById(R.id.action_admin).setVisibility(View.GONE);
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }else {
                    findViewById(R.id.action_admin).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }


    private void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


}
