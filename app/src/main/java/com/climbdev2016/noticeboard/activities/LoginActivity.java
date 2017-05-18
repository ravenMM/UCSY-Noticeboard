package com.climbdev2016.noticeboard.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;

import com.climbdev2016.noticeboard.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private FirebaseUser mUser;

    private ProgressDialog mProgressDialog;
    LoginButton fbLoginBtn;
    CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        mUserRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.child_users));
        mUserRef.keepSynced(true);

        callbackManager = CallbackManager.Factory.create();

        mProgressDialog = new ProgressDialog(this);

        cardView = (CardView) findViewById(R.id.card_view);

        fbLoginBtn = (LoginButton) findViewById(R.id.fb_login_button);
        fbLoginBtn.setReadPermissions("email");
        fbLoginBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = mAuth.getCurrentUser();
                if (mUser != null){
                    goToMain();
                }
            }
        };

    }

    private void handleAccessToken(AccessToken accessToken) {
        mProgressDialog.setMessage(getString(R.string.login_txt));
        mProgressDialog.show();

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()){
                    Toasty.error(LoginActivity.this, getString(R.string.login_error_txt), Toast.LENGTH_LONG).show();
                }
                mProgressDialog.dismiss();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    private void goToMain() {
        final String user_key = mUser.getUid();

        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent intent = null;
                if (dataSnapshot.hasChild(user_key)){
                    intent = new Intent(LoginActivity.this,MainActivity.class);
                }else {
                    intent = new Intent(LoginActivity.this,RegisterActivity.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
        fbLoginBtn.setVisibility(View.GONE);
        cardView.setVisibility(View.GONE);
    }
}
