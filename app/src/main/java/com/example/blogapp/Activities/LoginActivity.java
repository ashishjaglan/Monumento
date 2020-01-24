package com.example.blogapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.blogapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private EditText mail, password;
    private Button loginBtn;
    private ProgressBar loginProgress;
    private FirebaseAuth mauth;
    private Intent HomeActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        mail=findViewById(R.id.loginMail);
        password=findViewById(R.id.loginPassword);
        loginBtn=findViewById(R.id.loginBtn);
        loginProgress=findViewById(R.id.loginprogress);
        mauth=FirebaseAuth.getInstance();
        HomeActivity=new Intent(this, com.example.blogapp.Activities.Home.class);

        loginProgress.setVisibility(View.INVISIBLE);

        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                loginBtn.setVisibility(View.INVISIBLE);
                loginProgress.setVisibility(View.VISIBLE);

                final String regMail=mail.getText().toString();
                final String regPassword=password.getText().toString();

                if(regMail.isEmpty() || regPassword.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    loginBtn.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);
                }else{
                    mauth.signInWithEmailAndPassword(regMail,regPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                           if(task.isSuccessful()){
                               loginBtn.setVisibility(View.VISIBLE);
                               loginProgress.setVisibility(View.INVISIBLE);
                               updateUI();
                           }else{
                               Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                           }
                        }
                    });
                }
            }
        });
    }

    private void updateUI() {
        startActivity(HomeActivity);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser muser=mauth.getCurrentUser();
        if(muser!=null){
            updateUI();
        }
    }
}
