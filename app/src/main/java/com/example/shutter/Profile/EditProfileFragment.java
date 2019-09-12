package com.example.shutter.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shutter.Dialogs.ConfirmPasswordDialog;
import com.example.shutter.R;
import com.example.shutter.Share.ShareActivity;
import com.example.shutter.Utils.FireBaseMethods;
import com.example.shutter.Utils.UniversalImageLoader;
import com.example.shutter.Models.UserAccountSettings;
import com.example.shutter.Models.UserSettings;
import com.example.shutter.Models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment implements ConfirmPasswordDialog.OnConfirmPasswordListener {

    @Override
    public void onConfirmPassword(String password) {
        Log.e(TAG, "onConfirmPassword: got the password: " + password);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
                AuthCredential credential = EmailAuthProvider
                        .getCredential(mAuth.getCurrentUser().getEmail(), password);

        // Prompt the user to re-provide their sign-in credentials
                mAuth.getCurrentUser().reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {

                                    Log.d(TAG, "User re-authenticated.");

                                    //Check to see if the email is already present in the database
                                    mAuth.fetchSignInMethodsForEmail(mEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                            if (task.isSuccessful()) {

                                                try {
                                                    if (task.getResult().getSignInMethods().size() == 1) {
                                                        Log.d(TAG, "onComplete: that email is already in use");
                                                        Toast.makeText(getActivity(), "That email is already in use.", Toast.LENGTH_SHORT).show();
                                                    }

                                                    else {
                                                        Log.d(TAG, "onComplete: that email is available.");

                                                        //the email is available
                                                        mAuth.getCurrentUser().updateEmail(mEmail.getText().toString())
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Log.d(TAG, "User email address updated.");
                                                                            Toast.makeText(getActivity(), "Email updated.", Toast.LENGTH_SHORT).show();
                                                                            mFireBaseMethods.updateEmail(mEmail.getText().toString());
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }

                                                catch(NullPointerException e) {
                                                    Log.e(TAG, "onComplete: NullPointerException: " + e.getMessage() );
                                                }
                                            }
                                        }
                                    });

                                }
                                
                                else
                                    Log.d(TAG, "onComplete: re-authentication failed");
                            }
                        });
    }

    private static final String TAG = "EditProfileFragment";
    private Context context;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FireBaseMethods mFireBaseMethods;

    private String userID;

    //EditProfile Fragment widgets
    private EditText mDisplayName, mUsername, mWebsite, mDescription, mEmail, mPhoneNumber;
    private TextView mChangeProfilePhoto;
    private CircleImageView mProfilePhoto;

    //variables
    private UserSettings mUserSettings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editprofile,container,false);
        mProfilePhoto = view.findViewById(R.id.profile_photo);
        mDisplayName = view.findViewById(R.id.display_name);
        mUsername= view.findViewById(R.id.username);
        mWebsite = view.findViewById(R.id.website);
        mDescription= view.findViewById(R.id.description);
        mEmail = view.findViewById(R.id.email);
        mPhoneNumber= view.findViewById(R.id.phoneNumber);
        context=getActivity();
        mChangeProfilePhoto= view.findViewById(R.id.changeProfilePhoto);
        mFireBaseMethods= new FireBaseMethods(getActivity());


        //setProfileImage();
        setupFirebaseAuth();

        //back arrow for navigating back to profileactivity.....

        ImageView backarrow =(ImageView)view.findViewById(R.id.backArrow);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to ProfileActivity.");
                getActivity().finish();
            }
        });

        ImageView checkmark = view.findViewById(R.id.saveChanges);
        checkmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: attempting to save changes.");
                saveProfileSetting();
                Toast.makeText(context,"Profile Sucessfully Updated",Toast.LENGTH_SHORT).show();
            }
        });

        return  view;
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
                if(!dataSnapshot.exists()) {
                    //add the username
                    mFireBaseMethods.updateUsername(username);
                    Toast.makeText(getActivity(), "Saved username.", Toast.LENGTH_SHORT).show();
                }

                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                    if(singleSnapshot.exists()) {
                        Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH " + singleSnapshot.getValue(Users.class).getUsername());
                        Toast.makeText(getActivity(), "That username already exists.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //setting profile widgets
    private void setProfileWidgets(UserSettings userSettings)
    {
        //Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase db "+userSettings.toString());
        //Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase db "+userSettings.getSettings().getUsername());

        //Users users = userSettings.getUsers();
        mUserSettings = userSettings;
        UserAccountSettings settings = userSettings.getSettings();
        UniversalImageLoader.setImage(settings.getProfile_photo(),mProfilePhoto,null,"");
        mDisplayName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
        mWebsite.setText(settings.getWebsite());
        mDescription.setText(settings.getDescription());
        mEmail.setText(userSettings.getUsers().getEmail());
        mPhoneNumber.setText(String.valueOf(userSettings.getUsers().getPhone_number()));
        mChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: changing profile photo");
                Intent intent = new Intent(getActivity(), ShareActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //268435456
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });
    }

    /*
     * Retrieves the data contained in the widgets and submits it to the database
     * Before doing so it checks to make sure that the username chosen is unique
     */
    private void saveProfileSetting() {
        final String displayName = mDisplayName.getText().toString();
        final String username = mUsername.getText().toString();
        final String website = mWebsite.getText().toString();
        final String description = mDescription.getText().toString();
        final String email = mEmail.getText().toString();
        final long phoneNumber = Long.parseLong(mPhoneNumber.getText().toString());

        //case 1: if the user made a change to the username
        if(!mUserSettings.getUsers().getUsername().equals(username)) {
            checkIfUsernameExists(username);
        }

        //case 2: if the user made a change to their email
        if(!mUserSettings.getUsers().getEmail().equals(email)) {

            //step 1: re-authenticate
            //  -Confirm the email and password

            ConfirmPasswordDialog dialog = new ConfirmPasswordDialog();
            dialog.show(getFragmentManager(), getString(R.string.confirm_password_dialog));
            dialog.setTargetFragment(EditProfileFragment.this, 1); //in which target this dialog closes

            //step 2: check if the email is already registered
            //  -'fetchProvidersForEmail(String email)'


            //step 3: submit the change
            //  -submit the new email to the database and authentication
        }

        /*
         * to change all the other settings that do not require uniqueness
         */
        if(!mUserSettings.getSettings().getDisplay_name().equals(displayName)) {
            //update displayname
            mFireBaseMethods.updateUserAccountSettings(displayName, null, null, 0);
        }

        if(!mUserSettings.getSettings().getWebsite().equals(website)) {
            //update website
            mFireBaseMethods.updateUserAccountSettings( null, website,null, 0);
        }

        if(!mUserSettings.getSettings().getDescription().equals(description)) {
            //update description
            mFireBaseMethods.updateUserAccountSettings( null, null, description,0);
        }

       if(! ((mUserSettings.getUsers().getPhone_number()) == (phoneNumber))) {
            //update phonenumber
            mFireBaseMethods.updateUserAccountSettings( null, null, null,phoneNumber);
        }

    }

    /*
     * Firebase
     */
    private void setupFirebaseAuth()
    {
        Log.d(TAG, "setupFirebaseAuth: stetting up firebase auth..");

        mAuth= FirebaseAuth.getInstance();
        mFirebaseDatabase= FirebaseDatabase.getInstance();
        myRef =mFirebaseDatabase.getReference();
        userID= mAuth.getCurrentUser().getUid();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override


            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user!=null)
                {
                    Log.d(TAG, "onAuthStateChanged: signed in:"+user.getUid());
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
                //retrieve user data from database
                setProfileWidgets(mFireBaseMethods.getUsersSettings(dataSnapshot));

                // retrieve image for user in question
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
