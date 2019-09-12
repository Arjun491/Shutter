package com.example.shutter.Login;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shutter.R;
import com.example.shutter.Utils.FireBaseMethods;
import com.example.shutter.Models.Users;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FireBaseMethods fireBaseMethods;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    private Context mContext;
    private EditText mEmail,mUsername,mPassword;

    private String email,username,password;
    private Button btnRegister;
    private SpinKitView mProgressBar;
    private String append="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext=RegisterActivity.this;
        initWidgets();
        init();
        Log.d(TAG, "onCreate: ");
        fireBaseMethods = new FireBaseMethods(mContext);

        mAuth=FirebaseAuth.getInstance();
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        myRef=mFirebaseDatabase.getReference();

        setupFirebaseAuth();
    }

    private void init()
    {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               email=mEmail.getText().toString();
               password=mPassword.getText().toString();
               username=mUsername.getText().toString();
               if(checkInputs(email,username,password))
               {
                   mProgressBar.setVisibility(View.VISIBLE);


                   fireBaseMethods.registerNewEmail(email,password,username);


                   mProgressBar.setVisibility(View.GONE);

               }
            }
        });

    }

    private boolean checkInputs(String email,String username, String password) {
        Log.d(TAG, "checkInputs: check input for null values");
        if(email.equals("")||username.equals("")||password.equals(""))
        {
            Toast.makeText(mContext,"You must fill out all the fields to register.",Toast.LENGTH_SHORT).show();
          return false;
        }
        else {
            return true;
        }

    }

    /*
     * initialize the widgets in the activity
     */
    private void initWidgets()
    {
        Log.d(TAG, "initWidgets: Initializing Widgets..");
        mEmail=findViewById(R.id.input_email);
        mPassword=findViewById(R.id.input_email);
        mUsername =findViewById(R.id.input_username);
        mProgressBar=findViewById(R.id.progressBar);
        btnRegister =findViewById(R.id.btn_register);

        mContext=RegisterActivity.this;
        mProgressBar.setVisibility(View.GONE);

        }

    //FIREBASE STARTED FROM HERE

    private boolean isStringNull(String string)
    {
        Log.d(TAG, "isStringNull: checking if string is null");
        if(string.equals(""))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    //setup the Firebase auth object

    private void setupFirebaseAuth()
    {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth..");
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override


            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if user is logged in
                if(user!=null)
                {
                    Log.d(TAG, "onAuthStateChanged: signed in:"+user.getUid());
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            checkIfUsernameExists(username);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    finish();
                }
                else
                {
                    Log.d(TAG, "onAuthStateChanged: signed out:");
                }

            }
        };
    }

    // onStart and onStop method //
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener!=null)
        {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /*
     *checking if @param username already exists in database
     * @param username
     */
    private void checkIfUsernameExists(final String username) {
        Log.d(TAG, "checkIfUsernameExists: checking if " + username + " already exists");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                    if(singleSnapshot.exists()) {
                        Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH " + singleSnapshot.getValue(Users.class).getUsername());
                        append = myRef.push().getKey().substring(3,10);
                        Log.d(TAG, "onDataChange: username already exists. Appending random string to name " + append);
                    }
                }
                String mUsername;
                mUsername= username + append;

                //add new user to the database
                fireBaseMethods.addNewUser(email, mUsername,"","","");

                Toast.makeText(mContext,"Sign up successful. Sending verification email.",Toast.LENGTH_SHORT).show();

                // sign out user until they got verified
                mAuth.signOut();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
