package com.climbdev2016.noticeboard.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.climbdev2016.noticeboard.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class RegisterActivity extends AppCompatActivity {


    private ImageView registerUserImage;
    private EditText registerUserName,registerUserOccupation;
    private static final int GALLERY_REQUEST = 1;
    private Button btnRegisterUser;
    private Uri mImageUri = null;
    private DatabaseReference mDatabaseUser;
    private FirebaseAuth mFirebaseAuth;
    private StorageReference mStorage;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users");
        mStorage = FirebaseStorage.getInstance().getReference().child("Profile_Images");
        registerUserImage = (ImageView) findViewById(R.id.register_user_image);
        registerUserName = (EditText) findViewById(R.id.register_user_name);
        registerUserOccupation = (EditText) findViewById(R.id.register_user_occupation);
        btnRegisterUser = (Button) findViewById(R.id.btnRegisterUser);
        mProgressDialog = new ProgressDialog(this);

        registerUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
            }
        });

        btnRegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startSetupAccount();
            }
        });


    }

    private void startSetupAccount() {

        final String name = registerUserName.getText().toString().trim();
        final String occupation = registerUserOccupation.getText().toString().trim();
        final String user_id = mFirebaseAuth.getCurrentUser().getUid();
        final String defaultImage = "https://quotefancy.com/media/wallpaper/3840x2160/1700729-Linus-Torvalds-Quote-Talk-is-cheap-Show-me-the-code.jpg";
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(occupation) && mImageUri!=null ){

            mProgressDialog.setMessage("Saving");
            mProgressDialog.show();

            StorageReference filepath = mStorage.child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUri = taskSnapshot.getDownloadUrl().toString();

                    mDatabaseUser.child(user_id).child("name").setValue(name);
                    mDatabaseUser.child(user_id).child("occupation").setValue(occupation);
                    mDatabaseUser.child(user_id).child("image").setValue(downloadUri);
                    mDatabaseUser.child(user_id).child("cover").setValue(defaultImage);

                    mProgressDialog.dismiss();
                    goToMain();
                }
            });
        }else {
            Toast.makeText(RegisterActivity.this,"Please fill all fields",Toast.LENGTH_SHORT).show();
        }

    }

    private void goToMain() {
        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            Uri imagUri = data.getData();

            CropImage.activity(imagUri).setCropShape(CropImageView.CropShape.OVAL)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1).start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                registerUserImage.setImageURI(mImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}


