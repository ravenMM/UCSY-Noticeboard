package com.climbdev2016.noticeboard.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.climbdev2016.noticeboard.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import es.dmoral.toasty.Toasty;


public class PostActivity extends AppCompatActivity
        implements MaterialSpinner.OnItemSelectedListener, View.OnTouchListener {

    private DatabaseReference mPostRef;
    private DatabaseReference mUserRef;
    private FirebaseUser mUser;

    private EditText txtPost;
    private MaterialSpinner spinner;
    private String postCategory = "Tutorials";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ptoolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        DatabaseReference mDbRef = FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mPostRef = mDbRef.child(getString(R.string.child_post));
        mUserRef = mDbRef.child(getString(R.string.child_users));

        spinner = (MaterialSpinner) findViewById(R.id.spinner);
        spinner.setItems(getResources().getStringArray(R.array.post_categories));
        spinner.setOnItemSelectedListener(this);

        txtPost = (EditText) findViewById(R.id.txt_post);
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

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        else if (item.getItemId()== R.id.action_post) {

            String uId = mUser.getUid();
            String postContent = txtPost.getText().toString().trim();
            String postTime = String.valueOf(-1 * System.currentTimeMillis());

            if (postContent.isEmpty() || postContent.equals("")) {

                Toasty.warning(this, "Write something to post...", Toast.LENGTH_SHORT, true).show();
            } else {
                final DatabaseReference newPostRef = mPostRef.push();
                newPostRef.child(getString(R.string.child_post_user_id)).setValue(uId);

                mUserRef.child(uId).child(getString(R.string.child_user_name)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        newPostRef.child(getString(R.string.child_post_user_name)).setValue(dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mUserRef.child(uId).child(getString(R.string.child_user_image)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        newPostRef.child(getString(R.string.child_post_user_profile_picture)).setValue(dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                newPostRef.child(getString(R.string.child_post_time)).setValue(postTime);
                newPostRef.child(getString(R.string.child_post_content)).setValue(postContent);
                newPostRef.child(getString(R.string.child_post_category)).setValue(postCategory);
                Toasty.success(PostActivity.this, "Posted", Toast.LENGTH_SHORT, true).show();
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
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
