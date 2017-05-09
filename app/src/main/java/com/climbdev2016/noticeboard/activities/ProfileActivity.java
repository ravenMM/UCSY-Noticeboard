package com.climbdev2016.noticeboard.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.climbdev2016.noticeboard.R;
import com.climbdev2016.noticeboard.adapters.ProfileStatusRecyclerAdapter;
import com.climbdev2016.noticeboard.adapters.StatusRecyclerAdapter;
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
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

public class ProfileActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    private FirebaseAuth mAuth;
    private Query currentUserPostQuery;
    private FirebaseUser mUser;
    private Uri profileUri = null;
    private Uri coverUri = null;
    private KenBurnsView cover;
    private CircularImageView profile;
    private TextView userName,userOccupation;
    private DatabaseReference mDatabaseUser;
    private static final int GALLERY_REQUEST = 1;
    private static final int GALLERY_REQUEST_COVER = 2;
    private StorageReference mStorageProfile,mStorageCover;
    private ProgressDialog mProgressDialog;
    private ProfileStatusRecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userName = (TextView) findViewById(R.id.name_tv);
        userOccupation = (TextView) findViewById(R.id.occupation_tv);
        cover = (KenBurnsView) findViewById(R.id.user_cover);
        profile = (CircularImageView) findViewById(R.id.user_profile);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mStorageProfile = FirebaseStorage.getInstance().getReference().child("Profile_Images");
        mStorageCover = FirebaseStorage.getInstance().getReference().child("Cover_Images");

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUser.keepSynced(true);

        mProgressDialog = new ProgressDialog(this);

        setData();


        currentUserPostQuery = FirebaseDatabase.getInstance().getReference().child("Post")
                .orderByChild("user_id").equalTo(mUser.getUid());
        mRecyclerView = (RecyclerView) findViewById(R.id.rc_profile_post);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter =new ProfileStatusRecyclerAdapter(this, currentUserPostQuery);
        mRecyclerView.setAdapter(adapter);

        final PullRefreshLayout mPullRefreshLayout = (PullRefreshLayout) findViewById(R.id.profileRefresh);
        mPullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(adapter);
                mPullRefreshLayout.setRefreshing(false);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callCameraAction(GALLERY_REQUEST);

            }
        });

        cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                callCameraAction(GALLERY_REQUEST_COVER);

            }
        });

    }

    private void callCameraAction(int request) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,request);
    }

    private void setData() {

        String user_key = mAuth.getCurrentUser().getUid();

        mDatabaseUser.child(user_key).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String user_name = dataSnapshot.getValue().toString();
                getSupportActionBar().setTitle(user_name);
                userName.setText(user_name);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mDatabaseUser.child(user_key).child("occupation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String occupation = dataSnapshot.getValue().toString();
                userOccupation.setText(occupation);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseUser.child(user_key).child("image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String imageUrl = dataSnapshot.getValue().toString();
                Glide.with(ProfileActivity.this).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(profile);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseUser.child(user_key).child("cover").addValueEventListener(new ValueEventListener() {
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
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){

            profileUri = data.getData();

            CropImage.activity(profileUri).setCropShape(CropImageView.CropShape.OVAL)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1).start(this);

        }
        if (requestCode == GALLERY_REQUEST_COVER && resultCode == RESULT_OK){
            coverUri = data.getData();
            uploadingPhoto(mStorageCover);

        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if (resultCode == RESULT_OK) {
                    profileUri= result.getUri();
                    uploadingPhoto(mStorageProfile);
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
                finish();
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



    private void uploadingPhoto(StorageReference mStorage) {
        mProgressDialog.setMessage("Uploading");
        mProgressDialog.show();
        if (mStorage == mStorageProfile){

            StorageReference filepath = mStorageProfile.child(profileUri.getLastPathSegment());
            filepath.putFile(profileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUri =taskSnapshot.getDownloadUrl().toString();

                    mDatabaseUser.child(mAuth.getCurrentUser().getUid()).child("image").setValue(downloadUri).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mProgressDialog.dismiss();
                        }
                    });

                }
            });

        }
        if (mStorage==mStorageCover){
            StorageReference filepath = mStorageCover.child(coverUri.getLastPathSegment());
            filepath.putFile(coverUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUri =taskSnapshot.getDownloadUrl().toString();

                    mDatabaseUser.child(mAuth.getCurrentUser().getUid()).child("cover").setValue(downloadUri);
                    mProgressDialog.dismiss();

                }
            });
        }


    }

}
