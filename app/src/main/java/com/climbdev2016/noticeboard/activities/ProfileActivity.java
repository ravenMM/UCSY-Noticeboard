package com.climbdev2016.noticeboard.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.climbdev2016.noticeboard.R;
import com.climbdev2016.noticeboard.adapters.ProfileStatusRecyclerAdapter;
import com.facebook.login.LoginManager;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import static com.climbdev2016.noticeboard.utils.Constants.CODE_COVER_GALLERY_REQUEST;
import static com.climbdev2016.noticeboard.utils.Constants.CODE_PROFILE_GALLERY_REQUEST;

public class ProfileActivity extends AppCompatActivity
        implements PullRefreshLayout.OnRefreshListener, View.OnClickListener{

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mUserRef;
    private StorageReference mProfileRef;
    private StorageReference mCoverRef;

    private Uri profileUri = null;
    private Uri coverUri = null;

    private KenBurnsView cover;
    private CircularImageView profile;
    private TextView userName;
    private TextView userOccupation;
    private PullRefreshLayout mPullRefreshLayout;
    private ProgressDialog mProgressDialog;

    private ProfileStatusRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        DatabaseReference mDbRef = FirebaseDatabase.getInstance().getReference();
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

        mProfileRef = mStorageRef.child(getString(R.string.child_profile_images));
        mCoverRef = mStorageRef.child(getString(R.string.child_cover_images));

        mUserRef = mDbRef.child(getString(R.string.child_users));
        mUserRef.keepSynced(true);
        Query currentUserPostQuery = mDbRef.child(getString(R.string.child_post))
                .orderByChild(getString(R.string.child_post_user_id)).equalTo(mUser.getUid());

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rc_profile_post);
        cover = (KenBurnsView) findViewById(R.id.user_cover);
        profile = (CircularImageView) findViewById(R.id.user_profile);
        userName = (TextView) findViewById(R.id.name_tv);
        userOccupation = (TextView) findViewById(R.id.occupation_tv);
        mPullRefreshLayout = (PullRefreshLayout) findViewById(R.id.profileRefresh);

        mProgressDialog = new ProgressDialog(this);

        setData();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter =new ProfileStatusRecyclerAdapter(this, currentUserPostQuery);
        mRecyclerView.setAdapter(adapter);

        mPullRefreshLayout.setOnRefreshListener(this);
        profile.setOnClickListener(this);
        cover.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.user_profile:
                callCameraAction(CODE_PROFILE_GALLERY_REQUEST);
                break;
            case R.id.user_cover:
                callCameraAction(CODE_COVER_GALLERY_REQUEST);
                break;
        }
    }

    private void callCameraAction(int request) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,request);
    }

    private void setData() {

        String userId = mUser.getUid();

        mUserRef.child(userId).child(getString(R.string.child_user_name)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String user_name = dataSnapshot.getValue().toString();
                userName.setText(user_name);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUserRef.child(userId).child(getString(R.string.child_user_occupation)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String occupation = dataSnapshot.getValue().toString();
                userOccupation.setText(occupation);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUserRef.child(userId).child(getString(R.string.child_user_image)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String imageUrl = dataSnapshot.getValue().toString();
                Glide.with(ProfileActivity.this).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(profile);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUserRef.child(userId).child("cover").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String coverUrl = dataSnapshot.getValue().toString();
                Glide.with(ProfileActivity.this).load(coverUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(cover);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_PROFILE_GALLERY_REQUEST && resultCode == RESULT_OK){

            profileUri = data.getData();

            CropImage.activity(profileUri).setCropShape(CropImageView.CropShape.OVAL)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1).start(this);

        }
        else if (requestCode == CODE_COVER_GALLERY_REQUEST  && resultCode == RESULT_OK){
            coverUri = data.getData();
            uploadPhoto(mCoverRef);

        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if (resultCode == RESULT_OK) {
                    profileUri= result.getUri();
                    uploadPhoto(mProfileRef);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
        }
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
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(ProfileActivity.this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void uploadPhoto(StorageReference mStorage) {
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.show();

        StorageReference filepath;
        final String userId = mUser.getUid();

        if (mStorage == mProfileRef){
            filepath = mProfileRef.child(profileUri.getLastPathSegment());
            filepath.putFile(profileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUri =taskSnapshot.getDownloadUrl().toString();
                    mUserRef.child(userId).child(getString(R.string.child_user_image)).setValue(downloadUri);
                }
            });
        }
        if (mStorage== mCoverRef){
            filepath = mCoverRef.child(profileUri.getLastPathSegment());
            filepath.putFile(coverUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUri =taskSnapshot.getDownloadUrl().toString();
                    mUserRef.child(userId).child(getString(R.string.child_user_cover)).setValue(downloadUri);
                }
            });
        }
        mProgressDialog.dismiss();
    }

    @Override
    public void onRefresh() {
        adapter.notifyDataSetChanged();
        mPullRefreshLayout.setRefreshing(false);
    }
}
