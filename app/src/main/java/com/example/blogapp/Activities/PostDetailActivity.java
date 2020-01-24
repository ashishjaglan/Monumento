package com.example.blogapp.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.blogapp.Adapters.CommentAdapter;
import com.example.blogapp.Models.Comment;
import com.example.blogapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    ImageView imgPost, imgUserPost, imgCurrentUser;
    TextView txtPostDesc, txtPostTitle, txtPostDateName;
    EditText editTextComment;
    String PostKey;
    Button addButton;

    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    FirebaseDatabase firebaseDatabase;
    RecyclerView rvComment;
    CommentAdapter commentAdapter;
    List<Comment> listComment;
    static String COMMENT_KEY="Comments";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Window w=getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getSupportActionBar().hide();

        rvComment=findViewById(R.id.recyclerView);

        imgPost=findViewById(R.id.post_detail_img);
        imgUserPost=findViewById(R.id.post_detail_user_img);
        imgCurrentUser=findViewById(R.id.post_detail_currentuser_img);

        txtPostDesc=findViewById(R.id.post_detail_desc);
        txtPostTitle=findViewById(R.id.post_detail_title);
        txtPostDateName=findViewById(R.id.post_detail_date_name);

        editTextComment=findViewById(R.id.post_detail_comment);

        addButton=findViewById(R.id.post_detail_add_comment);

        firebaseAuth=FirebaseAuth.getInstance();
        currentUser=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addButton.setVisibility(View.INVISIBLE);
                DatabaseReference commentReference=firebaseDatabase.getReference(COMMENT_KEY).child(PostKey).push();
                String editComment=editTextComment.getText().toString();
                String uid=currentUser.getUid();
                String userImg=currentUser.getPhotoUrl().toString();
                String userName=currentUser.getDisplayName();

                Comment comment=new Comment(editComment,uid,userImg,userName);

                commentReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PostDetailActivity.this, "comment added", Toast.LENGTH_SHORT).show();
                        editTextComment.setText("");
                        addButton.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PostDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        addButton.setVisibility(View.VISIBLE);
                    }
                });
            }
        });


        String postimg=getIntent().getExtras().getString("postImg");
        Glide.with(this).load(postimg).into(imgPost);

        String postUserImg=getIntent().getExtras().getString("userPic");
        Glide.with(this).load(postUserImg).into(imgUserPost);

        String postTitle=getIntent().getExtras().getString("title");
        txtPostTitle.setText(postTitle);

        String postDesc=getIntent().getExtras().getString("description");
        txtPostDesc.setText(postDesc);

        Glide.with(this).load(currentUser.getPhotoUrl()).into(imgCurrentUser);

        PostKey=getIntent().getExtras().getString("postKey");

        String date=timeStamptoString(getIntent().getExtras().getLong("postDate"));
        txtPostDateName.setText(date);

        inirvComment();

    }

    private void inirvComment() {
        rvComment.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference commentRef=firebaseDatabase.getReference(COMMENT_KEY).child(PostKey);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listComment=new ArrayList<>();
                for(DataSnapshot cmtsnap:dataSnapshot.getChildren()){
                    Comment comment=cmtsnap.getValue(Comment.class);
                    listComment.add(comment);
                }
                commentAdapter=new CommentAdapter(getApplicationContext(),listComment);
                rvComment.setAdapter(commentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String timeStamptoString(long time){
        Calendar calendar=Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);

        String date= DateFormat.format("dd-MM-yyyy",calendar).toString();
        return date;
    }

}
