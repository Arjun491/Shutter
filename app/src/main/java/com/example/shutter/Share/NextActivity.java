package com.example.shutter.Share;

import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shutter.R;
import com.example.shutter.Utils.FireBaseMethods;
import com.example.shutter.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NextActivity extends AppCompatActivity {

    //firebase
    private static final String TAG="NextActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FireBaseMethods mFireBaseMethods;

    //vars
    private String mAppend = "file:/";
    private int imageCount = 0;

    //widgets
    private EditText mCaption;
    private String imgUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        mFireBaseMethods = new FireBaseMethods(NextActivity.this);
        mCaption = findViewById(R.id.caption);
        setupFirebaseAuth();

        ImageView backArrow = findViewById(R.id.ivBackArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the next activity.");
                finish();
            }
        });


        TextView share = findViewById(R.id.ivShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to the final share screen.");
                //uploading image to firebase
                Toast.makeText(NextActivity.this, "Attempting to upload new photo", Toast.LENGTH_SHORT).show();
                String caption = mCaption.getText().toString();
                mFireBaseMethods.uploadNewPhoto(getString(R.string.new_photo), caption, imageCount, imgUrl);
            }
        });

        setImage();
    }

    /*
     * This method adds the photos to the firebase console and store them
     */
    private void someMethod()
    {
        //Step 1:
        //Create a data model for photos

        //Step 2:
        //Add properties to the Photo objects (caption, date, imageUrl, photo_id), tags, user_id


        //Step 3:
        //Count the number of photos that the user already has

        //Step 4:
        //a) Upload the photo to Firebase Storage
        //b) Insert into 'photos' node
        //c) Insert into 'user_photos' node



    }

    /*
     * This method gets the image url from the incoming intent and displays the chosen image
     */
    private void setImage(){
        Intent intent= getIntent();
        ImageView image=  findViewById(R.id.imageShare);
        imgUrl = intent.getStringExtra("Selected Image");
        UniversalImageLoader.setImage(imgUrl, image,null, mAppend);
    }

    /** ----------- Firebase ------------------ **/
    private void setupFirebaseAuth()
    {

        Log.d(TAG, "setupFirebaseAuth: stetting up firebase auth..");

        mAuth= FirebaseAuth.getInstance();
        mFirebaseDatabase= FirebaseDatabase.getInstance();
        myRef =mFirebaseDatabase.getReference();
        Log.d(TAG, "onDataChange: image count: " + imageCount);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override


            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if(user!=null)
                {
                    Log.d(TAG, "onAuthStateChanged: signed in: " + user.getUid());
                }
                else
                {
                    Log.d(TAG, "onAuthStateChanged: signed out:");
                }

            }
        };
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imageCount = mFireBaseMethods.getImageCount(dataSnapshot);
                Log.d(TAG, "onDataChange: image count: " + imageCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // onStart and onStop method //
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener!=null)
        {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
