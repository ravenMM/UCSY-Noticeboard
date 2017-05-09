package com.climbdev2016.noticeboard.activities;

import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;


public class PostActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DatabaseReference mDatabaseReference;
    private EditText txtPost;
    private FirebaseUser mUser;
    private MaterialSpinner spinner;
    private DatabaseReference mDatabaseUser;
    private String category = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        toolbar = (Toolbar) findViewById(R.id.ptoolbar);

        if (toolbar!=null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Post");
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users");

        spinner = (MaterialSpinner) findViewById(R.id.spinner);
        spinner.setItems("Select Category","Tutorials","Assignments","Events","Reports","Others");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                spinner.setText((CharSequence) item);
                category = spinner.getText().toString();
                if (category.equals("Select Category")){
                    Toast.makeText(PostActivity.this,"Please Choose Others", Toast.LENGTH_SHORT).show();
                }

            }
        });

        spinner.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {
            @Override
            public void onNothingSelected(MaterialSpinner spinner) {
                category ="Select Category";
            }
        });


        txtPost = (EditText) findViewById(R.id.txt_post);

        txtPost.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_write_post,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.action_post) {

            String uId = mUser.getUid();
            String text = txtPost.getText().toString();
            String time = String.valueOf(-1 * System.currentTimeMillis());

            try {
                if (category.equals("Select Category") && category==null){
                    Toast.makeText(PostActivity.this, "Please Choose One", Toast.LENGTH_SHORT).show();
                }else {

                    final DatabaseReference newPost = mDatabaseReference.push();
                    newPost.child("user_id").setValue(uId);

                    mDatabaseUser.child(uId).child("name").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newPost.child("user_name").setValue(dataSnapshot.getValue());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    mDatabaseUser.child(uId).child("image").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newPost.child("user_profile_picture").setValue(dataSnapshot.getValue());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    newPost.child("time").setValue(time);
                    newPost.child("content").setValue(text);
                    newPost.child("category").setValue(category);
                    Toast.makeText(PostActivity.this, "Posted", Toast.LENGTH_LONG).show();
                    finish();
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
        return super.onOptionsItemSelected(item);
    }
}
