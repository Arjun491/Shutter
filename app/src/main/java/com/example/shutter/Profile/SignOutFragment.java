package com.example.shutter.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shutter.Login.LoginActivity;
import com.example.shutter.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignOutFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "SignOutFragment";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ProgressBar mProgressBar;
    private TextView tvSignOut;
    private Button btnConfirmSignOut;
    


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signout,container,false);
        mAuth=FirebaseAuth.getInstance();

        tvSignOut= view.findViewById(R.id.tvConfirmSignout);
        btnConfirmSignOut= view.findViewById(R.id.btnConfirmSignout);
        mProgressBar= view.findViewById(R.id.progressBar);
        btnConfirmSignOut.setOnClickListener(this);
        mProgressBar.setVisibility(View.GONE);
        setupFirebaseAuth();

        return view;
    }

    @Override
    public void onClick(View v) {
    switch (v.getId())
    {
        case R.id.btnConfirmSignout:

            Log.d(TAG, "onClick: attempt to sign out ");
            mProgressBar.setVisibility(View.VISIBLE);
            mAuth.signOut();
            getActivity().finish();
            mProgressBar.setVisibility(View.GONE);
            break;

    default:

    }
    }



    // FIREBASE STARTED FROM HERE


    //setup the Firebase auth object

    private void setupFirebaseAuth()
    {
        Log.d(TAG, "setupFirebaseAuth: stetting up firebase auth..");
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override


            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if(user!=null)
                {
                    Log.d(TAG, "onAuthStateChanged: signed in:" + user.getUid());
                }
                else
                {
                    Log.d(TAG, "onAuthStateChanged: signed out:");

                    Log.d(TAG, "onAuthStateChanged: navigating back to login screen");
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    /*
                    *   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    * Really important method:
                    *  to clear all the activity stacks
                    * */
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
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
    public void onStop() {
        super.onStop();
        if(mAuthListener!=null)
        {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
