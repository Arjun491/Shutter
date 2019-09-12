package com.example.shutter.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.shutter.Home.HomeActivity;
import com.example.shutter.Models.Photo;
import com.example.shutter.Profile.AccountSettingsActivity;
import com.example.shutter.R;
import com.example.shutter.Models.Users;
import com.example.shutter.Models.UserAccountSettings;
import com.example.shutter.Models.UserSettings;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FireBaseMethods {
    private static final String TAG = "FireBaseMethods";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseDatabase mFirebasedatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;
    private String userID;

    //vars
    private Context mContext;
    private double mPhotoUploadProgress = 0;

    public FireBaseMethods(Context context) {
        mContext=context;
        mAuth=FirebaseAuth.getInstance();
        mFirebasedatabase=FirebaseDatabase.getInstance();
        myRef=mFirebasedatabase.getReference();
        mStorageReference= FirebaseStorage.getInstance().getReference();

        if(mAuth.getCurrentUser()!=null)
        {
            userID=mAuth.getCurrentUser().getUid(); // return the user id from firebase
        }
    }

    public int getImageCount(DataSnapshot dataSnapshot) {
        int count = 0;
        for(DataSnapshot ds: dataSnapshot
                .child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .getChildren()) {

            count++;
        }

        return count;
    }

    public void uploadNewPhoto(final String photoType, final String caption, int count, final String imgUrl) {
        Log.d(TAG, "uploadNewPhoto: attempting to upload new photo");

        Filepath filepath = new Filepath();

        //case1: new photo
        if(photoType.equals(mContext.getString(R.string.new_photo))) {
            Log.d(TAG, "uploadNewPhoto: uploading new photo.");

            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final StorageReference storageReference = mStorageReference.child(filepath.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo" + (count + 1));

            //convert image url to bitmap
            Bitmap bm = ImageManager.getBitmap(imgUrl);
            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri firebaseUrl = task.getResult();
                        Toast.makeText(mContext, "Photo upload successful.", Toast.LENGTH_SHORT).show();

                        //add the new photo to 'photos' node and 'user_photos' node
                        addPhotoToDatabase(caption, firebaseUrl.toString());

                        //navigate to the main feed so the user can see their photo
                        Intent intent = new Intent(mContext, HomeActivity.class);
                        mContext.startActivity(intent);

                    } else {
                        Log.d(TAG, "onFailure: photo upload failed");
                        Toast.makeText(mContext, "Photo upload failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress= (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if(progress - 15 > mPhotoUploadProgress) {
                        Toast.makeText(mContext, "Photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }
                    Log.d(TAG, "onProgress: upload progress: " + progress);
                }
            });

        }

        //case2: new profile photo
        else if(photoType.equals(mContext.getString(R.string.profile_photo))) {
            Log.d(TAG, "uploadNewPhoto: uploading new profile photo");

            ((AccountSettingsActivity)mContext).setViewPager(
                    ((AccountSettingsActivity)mContext).pagerAdapter
                            .getFragmentNumber(mContext.getString(R.string.edit_profile_fragment))
            );

            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final StorageReference storageReference = mStorageReference.child(filepath.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo");

            //convert image url to bitmap
            Bitmap bm = ImageManager.getBitmap(imgUrl);
            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri firebaseUrl = task.getResult();
                        Toast.makeText(mContext, "Photo upload successful.", Toast.LENGTH_SHORT).show();

                        //insert into the 'user_account_settings' node
                        setProfilePhoto(firebaseUrl.toString());


                    } else {
                        Log.d(TAG, "onFailure: photo upload failed");
                        Toast.makeText(mContext, "Photo upload failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress= (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if(progress - 15 > mPhotoUploadProgress) {
                        Toast.makeText(mContext, "Photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }
                    Log.d(TAG, "onProgress: upload progress: " + progress);
                }
            });
        }
    }

    private void setProfilePhoto(String url) {
        Log.d(TAG, "setProfilePhoto: setting new profile image: " + url);
        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.profile_photo))
                .setValue(url);
    }

    private String getTimestamp(){
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
    }

    private void addPhotoToDatabase(String caption, String url) {
        Log.d(TAG, "addPhotoToDatabase: adding photo to database");

        String tags = StringManipulation.getTags(caption);
        String newPhotoKey = myRef.child(mContext.getString(R.string.dbname_photos)).push().getKey();
        Photo photo = new Photo();
        photo.setCaption(caption);
        photo.setDate_created(getTimestamp());
        photo.setImage_path(url);
        photo.setTags(tags);
        photo.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        photo.setPhoto_id(newPhotoKey);

        //insert into database
        myRef.child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(newPhotoKey).setValue(photo);

        myRef.child(mContext.getString(R.string.dbname_photos))
                .child(newPhotoKey).setValue(photo);
    }

    /*
     * Update the 'user_account_settings' node in the firebase
     * @param displayName
     * @param website
     * @param description
     * @param phoneNumber
     */
    public void updateUserAccountSettings(String display_name, String website, String description, long phoneNumber) {
        Log.d(TAG, "updateUserAccountSettings: updating user account settings");

        if(display_name != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_display_name))
                    .setValue(display_name);
        }

        if(website != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_website))
                    .setValue(website);
        }

        if(description != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_description))
                    .setValue(description);
        }


        if(phoneNumber != 0) {
            myRef.child(mContext.getString(R.string.dbname_users))
                    .child(userID)
                    .child(mContext.getString(R.string.field_phone_number))
                    .setValue(phoneNumber);
        }



    }


    /* we have an updated method
    public boolean checkIfUsernameExists(String username, DataSnapshot dataSnapshot)
    {
        Log.d(TAG, "checkIfUsernameExists: checking if " + username + " already exists");
        Users users = new Users();
        //loop through datasnapshot(It contains all the nodes) to look everything
        for (DataSnapshot ds: dataSnapshot.child(userID).getChildren() )
        {
            Log.d(TAG, "checkIfUsernameExists: datasnapshots"+ds);
            users.setUsername(ds.getValue(Users.class).getUsername());
            Log.d(TAG, "checkIfUsernameExists: username:"+ users.getUsername());
            if(StringManipulation.expandUsername(users.getUsername()).equals(username))
            {
                Log.d(TAG, "checkIfUsernameExists: Found a match"+ users.getUsername());
                Toast.makeText(mContext,"Users Already Exists",Toast.LENGTH_LONG).show();
                return true;
            }
        }
        return false;
        
    }
    */


    /**
     * Register a new email and password to Firebase Authentication
     * @param email
     * @param password
     * @param username
     */
    public void registerNewEmail(final String email, String password, final String username){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();

                        }
                        else if(task.isSuccessful()){

                            // for verification call this method here
                            ////then  we will sign out user immediately after calling  sendVerificationEmail(); here
                            // becuase we dont want user to get register utill they get verified
                            // this feature works in RegisterActivity
                            sendVerificationEmail();

                            userID = mAuth.getCurrentUser().getUid();
                            Log.d(TAG, "onComplete: Authstate changed: " + userID);

                            //add user_id field under user
//                            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
//                                    .child(userID)
//                                    .child(mContext.getString(R.string.field_user_id))
//                                    .setValue(userID);
                        }

                    }
                });
    }



    /*
    * Users will not be able to login to the account unless they verify their email
    */

    private void sendVerificationEmail()
    {
        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null)
        { user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()==true)
                {
                    // then  go to register new email method... //

                    Log.d(TAG, "onComplete: register new email method");
                }
                else
                {
                    Toast.makeText(mContext,"Couldn't send verification email! 404: ",Toast.LENGTH_SHORT).show();
                }
            }
        });

        }
    }


    //.. insert data to user_account_settings and user node
    public void addNewUser(String email, String username, String description, String website, String profile_photo){

        Users users = new Users( userID,  1,  email,  StringManipulation.condenseUsername(username) );

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .setValue(users);

    UserAccountSettings settings = new UserAccountSettings(
            description,
            username,
            0,
            0,
            0,
            profile_photo,
            StringManipulation.condenseUsername(username),
            website,
            userID
    );

    myRef.child(mContext.getString(R.string.dbname_user_account_settings))
            .child(userID)
            .setValue(settings);

}

    //updates the username in the user's and user_account_settings's database after change
    public void updateUsername(String username) {
        Log.d(TAG, "updateUsername: updating username to " + username);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);
    }

    //updates the email in the user's node in firebase database after change
    public void updateEmail(String email) {
        Log.d(TAG, "updateEmail: updating email to " + email);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_email))
                .setValue(email);
    }

    // retrieves account settings for user currently logged in from user_account_settings

    public UserSettings getUsersSettings(DataSnapshot dataSnapshot)
    {
        Log.d(TAG, "getUserAccountSettings: retrieving users account settings from firebase ::");

        UserAccountSettings settings = new UserAccountSettings();
        Users users = new Users();

        for ( DataSnapshot ds:dataSnapshot.getChildren())
        {
            // here key is user_account_settings node
           if(ds.getKey().equals(mContext.getString(R.string.dbname_user_account_settings)))
           {
               Log.d(TAG, "getUserAccountSettings:datasnapshot "+ds.child(userID));
               try {

               settings.setDisplay_name(
                       ds.child(userID)
                       .getValue(UserAccountSettings.class)
                       .getDisplay_name()

               );

               settings.setUsername(
                       ds.child(userID)
                       .getValue(UserAccountSettings.class)
                       .getUsername()

               );

               settings.setWebsite(
                       ds.child(userID)
                       .getValue(UserAccountSettings.class)
                       .getWebsite()

               );

               settings.setDescription(
                       ds.child(userID)
                               .getValue(UserAccountSettings.class)
                               .getDescription()

               );

               settings.setProfile_photo(
                       ds.child(userID)
                               .getValue(UserAccountSettings.class)
                               .getProfile_photo()

               );
               settings.setPosts(
                       ds.child(userID)
                               .getValue(UserAccountSettings.class)
                               .getPosts()

               );
               settings.setFollowing(
                       ds.child(userID)
                               .getValue(UserAccountSettings.class)
                               .getFollowing()

               );
               settings.setFollowers(
                       ds.child(userID)
                               .getValue(UserAccountSettings.class)
                               .getFollowers()

               );
//                   settings.setUser_id(
//                           ds.child(userID)
//                                   .getValue(UserAccountSettings.class)
//                                   .getUser_id()
//
//                   );


                   Log.d(TAG, "getUserAccountSettings: retrieved user_account_settings information"+settings.toString());


               }
               catch (NullPointerException ex)
               {
                   Log.d(TAG, "getUserAccountSettings: NullPointerException"+ex.getMessage());
               }
           }

            // here key is users node
            if(ds.getKey().equals(mContext.getString(R.string.dbname_users)))

            {

                users.setUsername(
                        ds.child(userID)
                                .getValue(Users.class)
                                .getUsername()

                );

                users.setEmail(
                        ds.child(userID)
                                .getValue(Users.class)
                                .getEmail()

                );

                users.setPhone_number(
                        ds.child(userID)
                                .getValue(Users.class)
                                .getPhone_number()

                );

                users.setUser_id(
                        ds.child(userID)
                                .getValue(Users.class)
                                .getUser_id()

                );
                Log.d(TAG, "getUserAccountSettings: retrieved users information " + settings.toString());

            }
        }
        return new UserSettings(users,settings);
    }
}
