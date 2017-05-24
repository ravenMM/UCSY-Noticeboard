package com.climbdev2016.noticeboard.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.climbdev2016.noticeboard.R;
import com.climbdev2016.noticeboard.adapters.ProfileStatusRecyclerAdapter;
import com.climbdev2016.noticeboard.models.User;
import com.climbdev2016.noticeboard.utils.Constants;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import es.dmoral.toasty.Toasty;

import static com.climbdev2016.noticeboard.utils.Constants.CODE_PROFILE_GALLERY_REQUEST;

public class ProfileActivity extends AppCompatActivity
        implements PullRefreshLayout.OnRefreshListener, View.OnClickListener{

    private static final String TAG = ProfileActivity.class.getSimpleName();

    private FirebaseAuth mAuth;
    private String userId;
    private DatabaseReference mUserRef;
    private CircularImageView userProfile;
    private TextView userName;
    private TextView userOccupation;
    private PullRefreshLayout mPullRefreshLayout;
    private Query currentUserPostQuery;
    private RecyclerView mRecyclerView;

    private ProfileStatusRecyclerAdapter mProfileAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
        }
        DatabaseReference mDbRef = Constants.FIREBASE_DATABASE_REFERENCE;
        mUserRef = mDbRef.child(getString(R.string.child_users));
        mUserRef.keepSynced(true);

        currentUserPostQuery = mDbRef.child(getString(R.string.child_post))
                .orderByChild(getString(R.string.child_post_user_id)).equalTo(userId);

        userProfile = (CircularImageView) findViewById(R.id.user_profile);
        userName = (TextView) findViewById(R.id.user_name);
        userOccupation = (TextView) findViewById(R.id.user_occupation);
        mPullRefreshLayout = (PullRefreshLayout) findViewById(R.id.profile_refresh);

        setUserData();

        mRecyclerView = (RecyclerView) findViewById(R.id.user_status_list);
        TextView signOut = (TextView) findViewById(R.id.sign_out);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProfileAdapter = new ProfileStatusRecyclerAdapter(this, currentUserPostQuery);
        mRecyclerView.setAdapter(mProfileAdapter);

        mPullRefreshLayout.setOnRefreshListener(this);
        signOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.sign_out:
                signOut();
                break;
        }
    }

    private void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(ProfileActivity.this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    private void setUserData() {
        mUserRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userName.setText(user.getName());
                userOccupation.setText(user.getOccupation());
                Glide.with(ProfileActivity.this)
                        .load(user.getImage()).diskCacheStrategy(DiskCacheStrategy.ALL).into(userProfile);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error " + databaseError);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_logout:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        mProfileAdapter.notifyDataSetChanged();
        mPullRefreshLayout.setRefreshing(false);
    }

}
