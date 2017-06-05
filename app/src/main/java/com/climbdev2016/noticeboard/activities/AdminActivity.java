package com.climbdev2016.noticeboard.activities;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.climbdev2016.noticeboard.R;
import com.climbdev2016.noticeboard.fragments.DeletePostsFragment;
import com.climbdev2016.noticeboard.fragments.PendingFragment;
import com.climbdev2016.noticeboard.fragments.UsersFragment;

public class AdminActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        getSupportActionBar().setTitle("Admin");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id==R.id.action_pending){
                    PendingFragment pending = new PendingFragment(getApplicationContext());
                    getSupportFragmentManager().beginTransaction().replace(R.id.admin_container,pending,"Pending Post").commit();
                    return true;

                }else if (id==R.id.action_users){
                    UsersFragment users = new UsersFragment(getApplicationContext());
                    getSupportFragmentManager().beginTransaction().replace(R.id.admin_container,users,"Users").commit();
                    return true;

                }else if (id==R.id.action_delete_posts){
                    DeletePostsFragment delete = new DeletePostsFragment(getApplicationContext());
                    getSupportFragmentManager().beginTransaction().replace(R.id.admin_container,delete,"Delete Post").commit();
                    return true;
                }
                return true;
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id==android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        PendingFragment pending = new PendingFragment(getApplicationContext());
        getSupportFragmentManager().beginTransaction().replace(R.id.admin_container,pending,"Pending Post").commit();
    }
}
