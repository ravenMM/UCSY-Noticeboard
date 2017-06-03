package com.climbdev2016.noticeboard.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.climbdev2016.noticeboard.R;
import com.climbdev2016.noticeboard.models.Post;
import com.climbdev2016.noticeboard.models.User;
import com.climbdev2016.noticeboard.utils.Constants;
import com.climbdev2016.noticeboard.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import es.dmoral.toasty.Toasty;

import static com.climbdev2016.noticeboard.utils.Constants.APPROVE_NO;
import static com.climbdev2016.noticeboard.utils.Constants.CHILD_POST;
import static com.climbdev2016.noticeboard.utils.Constants.CHILD_USER;
import static com.climbdev2016.noticeboard.utils.Constants.FIREBASE_DB_REF;


public class PostActivity extends AppCompatActivity
        implements MaterialSpinner.OnItemSelectedListener, View.OnTouchListener {

    private DatabaseReference mPostRef;
    private FirebaseUser mUser;
    private DatabaseReference mUserRef;
    private String userId;
    private String userName;
    private String userProfileUrl;
    private String postCategory = "Tutorials";
    private String postApprove;
    private EditText txtPost;
    private MaterialSpinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mUserRef = FIREBASE_DB_REF.child(CHILD_USER);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            userId = mUser.getUid();
            userName = mUser.getDisplayName();
            userProfileUrl = mUser.getPhotoUrl().toString();
        }

        mPostRef = FIREBASE_DB_REF.child(CHILD_POST);

        spinner = (MaterialSpinner) findViewById(R.id.category_spinner);
        spinner.setItems(getResources().getStringArray(R.array.post_categories));
        spinner.setOnItemSelectedListener(this);

        txtPost = (EditText) findViewById(R.id.status_text);
        txtPost.setOnTouchListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_post:
                post();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void post() {
        final String postContent = txtPost.getText().toString().trim();
        if (!Utils.isOnline(this)) {
            Toasty.error(this, "You are offline!", Toast.LENGTH_SHORT, true).show();
        } else if (TextUtils.isEmpty(postContent)) {
            Toasty.warning(this, "Write something first!", Toast.LENGTH_SHORT, true).show();
        } else {
            userId = mUser.getUid();
            mUserRef.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    userName = user.getName();
                    userProfileUrl = user.getImage();
                    postApprove = APPROVE_NO;
                    Post newPost = new Post(
                            userId, userName, userProfileUrl,
                            String.valueOf(System.currentTimeMillis()), postContent, postCategory,postApprove
                    );
                    mPostRef.push().setValue(newPost);
                    Toasty.success(PostActivity.this, "Posted", Toast.LENGTH_SHORT, true).show();
                    finish();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
        spinner.setText((CharSequence) item);
        postCategory = spinner.getText().toString();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                v.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return false;
    }
}
