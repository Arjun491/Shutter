package com.example.shutter.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.shutter.Models.Comment;
import com.example.shutter.Models.Like;
import com.example.shutter.Models.Photo;
import com.example.shutter.R;
import com.example.shutter.Utils.BottomNavigationViewHelper;
import com.example.shutter.Utils.FireBaseMethods;
import com.example.shutter.Utils.GridImageAdapter;
import com.example.shutter.Utils.UniversalImageLoader;
import com.example.shutter.Models.UserAccountSettings;
import com.example.shutter.Models.UserSettings;
import com.example.shutter.Models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    public interface  OnGridImageSelectedListener
    {
        void onGridImageSelected(Photo photo, int activityNumber);
    }
    OnGridImageSelectedListener  mOnGridImageSelectedListener;

    public static final int ACTIVITY_NUM =4;
    public static final int NUM_GRIDS_COLUMNS =3;
    public int uploadedimagesize=0;
    private TextView mPosts, mFollowers, mFollowing, mDisplayName,
    mUsername, mWebsite, mDescription;

    private ProgressBar mProgressBar;
    private CircleImageView mProfilePhoto;
    private GridView gridView;
    private Toolbar toolbar;
    private ImageView profileMenu;
    private Context mContext;

    private BottomNavigationViewEx bottomNavigationView;


    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FireBaseMethods mFireBaseMethods;


    //variable
   // private Users mUser;
    private int mFollowersCount =0;
    private int mFollowingCount =0;
    private int mPostCount =0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        mDisplayName =view.findViewById(R.id.display_name);
        mUsername =view.findViewById(R.id.username);
        mWebsite =view.findViewById(R.id.website);
        mDescription =view.findViewById(R.id.email);
        mProfilePhoto = view.findViewById(R.id.profile_photo);
        mPosts =view.findViewById(R.id.tvPosts);
        mFollowers =view.findViewById(R.id.tvFollowers);
        mFollowing =view.findViewById(R.id.tvFollowing);
        mProgressBar = view.findViewById(R.id.profileProgressBar);

        gridView=view.findViewById(R.id.gridView);

        toolbar=view.findViewById(R.id.profileToolBar);
        profileMenu=view.findViewById(R.id.profileMenu);
        mContext=getActivity();
        bottomNavigationView=view.findViewById(R.id.bottomNavViewBar);
        mFireBaseMethods = new FireBaseMethods(getActivity());

        Log.d(TAG, "onCreateView: started");
        setupBottomNaviagtionView();
        setupToolBar();
        setupFirebaseAuth();
        setupGridView();
        getFollowersCount();
        getFollowingCount();
        getPostCount();

        TextView editProfile = view.findViewById(R.id.textEditProfile);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: navigating to " + mContext.getString(R.string.edit_profile_fragment));
                Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
                intent.putExtra(getString(R.string.calling_activity), getString(R.string.profile_activity));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        try
        {
            mOnGridImageSelectedListener =(OnGridImageSelectedListener)getActivity();
        }
        catch (ClassCastException ex)
        {
            Log.d(TAG, "onAttach: ClassCastException"+ ex.getMessage());
        }
        super.onAttach(context);
    }

    /*
    *a method to setup the grid
    *
    * */
private void setupGridView()
{
    Log.d(TAG, "setupGridView: Setting up image grid view .");

    // a query to get images from firebase.. //

    final ArrayList<Photo> photos = new ArrayList<>();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    Query query = reference.
            child(getString(R.string.dbname_user_photos))
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    query.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                Photo photo = new Photo();
                Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
                try {

                    photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                    photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                    photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                    photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                    photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                    photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());

                    ArrayList<Comment> comments = new ArrayList<>();
                    for (DataSnapshot dataSnapshot1 : singleSnapshot.child(getString(R.string.field_comments)).getChildren()) {
                        Comment comment = new Comment();
                        comment.setUser_id(dataSnapshot1.getValue(Comment.class).getUser_id());
                        comment.setComment(dataSnapshot1.getValue(Comment.class).getComment());
                        comment.setDate_created(dataSnapshot1.getValue(Comment.class).getDate_created());
                        comments.add(comment);
                    }

                    photo.setComments(comments);
                    List<Like> likeList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot1 : singleSnapshot.child(getString(R.string.field_likes)).getChildren()) {
                        Like like = new Like();
                        like.setUser_id(dataSnapshot1.getValue(Like.class).getUser_id());
                        likeList.add(like);
                    }
                    photo.setLikes(likeList);
                    photos.add(photo);
                } catch (NullPointerException e) {
                    Log.e(TAG, "onDataChange: NullPOintException: " + e.getMessage());
                }
            }

                //setup image grid
            uploadedimagesize=photos.size();
            int gridWidth = getResources().getDisplayMetrics().widthPixels;
            int imageWidth  = gridWidth/NUM_GRIDS_COLUMNS;
            gridView.setColumnWidth(imageWidth); // 3 columns for grid view
            ArrayList<String> imageUrls = new ArrayList<>();

            // show images in order of from  latest to oldest one
            for(int i = photos.size()-1; i >=0; i--){
                imageUrls.add(photos.get(i).getImage_path());
            }
            GridImageAdapter adapter = new GridImageAdapter(getActivity(),R.layout.layout_grid_imageview,
                    "",imageUrls);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    mOnGridImageSelectedListener.onGridImageSelected(photos.get((uploadedimagesize-1)-position),ACTIVITY_NUM);
                }
            });

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.d(TAG, "onCancelled: query cancelled");
        }
    });


}

    private void setProfileWidgets(UserSettings userSettings)
    {
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase db "+userSettings.toString());
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase db "+userSettings.getSettings().getUsername());
        Users users = userSettings.getUsers();
        UserAccountSettings settings = userSettings.getSettings();
        UniversalImageLoader.setImage(settings.getProfile_photo(),mProfilePhoto,null,"");
        mDisplayName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
        mWebsite.setText(settings.getWebsite());
        mDescription.setText(settings.getDescription());

        mProgressBar.setVisibility(View.GONE);
    }

       private void setupToolBar()
       {
           ((ProfileActivity)getActivity()).setSupportActionBar(toolbar);

           profileMenu.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Log.d(TAG, "onClick: naviagting to account settings.");
                   Intent intent = new Intent(mContext, AccountSettingsActivity.class);
                   startActivity(intent);

               }
           });

        }







        // FIREBASE STARTED FROM HERE


    //setup the Firebase auth object

    private void setupFirebaseAuth()
    {
        Log.d(TAG, "setupFirebaseAuth: stetting up firebase auth..");

        mAuth=FirebaseAuth.getInstance();
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        myRef =mFirebaseDatabase.getReference();

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

    private void setupBottomNaviagtionView()
    {
        Log.d(TAG, "setupBottomNaviagtionView: setting up bottomNaviagtionView");


        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView);

        BottomNavigationViewHelper.enableNavigation(mContext,getActivity(),bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = ((Menu) menu).getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


    private void getFollowersCount(){
        mFollowersCount = 0;
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_followers))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());      query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found followers:" + singleSnapshot.getValue());

                    mFollowersCount++;


                }
                mFollowers.setText(String.valueOf(mFollowersCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void getFollowingCount(){
        mFollowingCount = 0;
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found following user:" + singleSnapshot.getValue());

                    mFollowingCount++;


                }
                mFollowing.setText(String.valueOf(mFollowingCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    private void getPostCount(){
        mPostCount = 0;
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());      query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found post:" + singleSnapshot.getValue());

                    mPostCount++;


                }
                mPosts.setText(String.valueOf(mPostCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }



}
