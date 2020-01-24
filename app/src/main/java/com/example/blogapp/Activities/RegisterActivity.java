package com.example.blogapp.Activities;

import android.Manifest;
import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.blogapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {

    ImageView regUserPic;
    static int PReqCode=1;
    static int REQUESCODE=1;
    Uri pickedImgUri;
    private FirebaseAuth mAuth;

    private EditText regName, regMail, regPassword, regPassword2;
    Button regBtn;
    ProgressBar regProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        regUserPic = findViewById(R.id.regUserPic);

        regName = findViewById(R.id.regName);
        regMail = findViewById(R.id.regMail);
        regPassword = findViewById(R.id.regPassword);
        regPassword2 = findViewById(R.id.regPassword2);

        regBtn = findViewById(R.id.regBtn);

        regProgress = findViewById(R.id.regprogress);

        regBtn.setVisibility(View.VISIBLE);
        regProgress.setVisibility(View.INVISIBLE);

        regUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>=22){
                    checkrequest();
                }else{
                    openGallery();
                }
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = regName.getText().toString();
                final String email = regMail.getText().toString();
                final String password = regPassword.getText().toString();
                final String password2 = regPassword2.getText().toString();

                regBtn.setVisibility(View.INVISIBLE);
                regProgress.setVisibility(View.VISIBLE);

                if(name.isEmpty() || email.isEmpty() || password.isEmpty() || password2.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "All fields are required !!", Toast.LENGTH_SHORT).show();
                    regBtn.setVisibility(View.VISIBLE);
                    regProgress.setVisibility(View.INVISIBLE);
                }else{
                    regBtn.setVisibility(View.INVISIBLE);
                    regProgress.setVisibility(View.VISIBLE);
                    register(name, email, password, password2);

                }
            }
        });

    }

    private void register(final String name, String email, String password, String password2) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Account created", Toast.LENGTH_SHORT).show();

                            updateUserInfo(name, pickedImgUri, mAuth.getCurrentUser());
                        }else{
                            Toast.makeText(RegisterActivity.this, "account creation failed", Toast.LENGTH_SHORT).show();
                            regBtn.setVisibility(View.VISIBLE);
                            regProgress.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    private void updateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser) {
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(RegisterActivity.this, "Registration complete", Toast.LENGTH_SHORT).show();
                                            updateIU();
                                        }
                                    }
                                });
                    }
                });
            }
        });
    }

    private void updateIU() {
        Intent homeActivity = new Intent(getApplicationContext(), Home.class);
        startActivity(homeActivity);
        finish();
    }

    private void openGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESCODE);

    }

    private void checkrequest() {

        if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            }else{
                ActivityCompat.requestPermissions(RegisterActivity.this,
                                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                                    PReqCode);
            }
        }else{
            openGallery();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK && requestCode== REQUESCODE && data!= null){
            pickedImgUri = data.getData();
            regUserPic.setImageURI(pickedImgUri);
        }
    }

}
