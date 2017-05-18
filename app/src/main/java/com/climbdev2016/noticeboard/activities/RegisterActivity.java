package com.climbdev2016.noticeboard.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.climbdev2016.noticeboard.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import es.dmoral.toasty.Toasty;

import static com.climbdev2016.noticeboard.utils.Constants.CODE_GALLERY_REQUEST;
import static com.climbdev2016.noticeboard.utils.Constants.DEFAULT_COVER_URL;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{


    private ImageView registerUserImage;
    private EditText registerUserName;
    private EditText registerUserOccupation;

    private Uri mImageUri = null;
    private ProgressDialog mProgressDialog;

    private DatabaseReference mDbUserRef;
    private FirebaseUser mUser;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDbUserRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.child_users));
        mStorage = FirebaseStorage.getInstance().getReference().child(getString(R.string.child_profile_images));
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mProgressDialog = new ProgressDialog(this);

        registerUserImage = (ImageView) findViewById(R.id.register_user_image);
        registerUserName = (EditText) findViewById(R.id.register_user_name);
        registerUserOccupation = (EditText) findViewById(R.id.register_user_occupation);
        Button btnRegisterUser = (Button) findViewById(R.id.btnRegisterUser);

        registerUserImage.setOnClickListener(this);
        btnRegisterUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegisterUser:
                startSetupAccount();
                break;
            case R.id.register_user_image:
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, CODE_GALLERY_REQUEST);
                break;
        }
    }

    private void startSetupAccount() {

        final String name = registerUserName.getText().toString().trim();
        final String occupation = registerUserOccupation.getText().toString().trim();
        final String user_id = mUser.getUid();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(occupation) && mImageUri != null ){
            mProgressDialog.setMessage(getString(R.string.registering_txt));
            mProgressDialog.show();

            StorageReference filepath = mStorage.child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUri = taskSnapshot.getDownloadUrl().toString();

                    mDbUserRef.child(user_id).child(getString(R.string.child_user_name)).setValue(name);
                    mDbUserRef.child(user_id).child(getString(R.string.child_user_occupation)).setValue(occupation);
                    mDbUserRef.child(user_id).child(getString(R.string.child_user_image)).setValue(downloadUri);

                    mProgressDialog.dismiss();
                    goToMain();
                }
            });
        }else {
            Toasty.warning(RegisterActivity.this,"Please fill all the fields.",Toast.LENGTH_SHORT).show();
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

        if (requestCode == CODE_GALLERY_REQUEST && resultCode == RESULT_OK){
            Uri imageUri = data.getData();

            CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.OVAL)
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


