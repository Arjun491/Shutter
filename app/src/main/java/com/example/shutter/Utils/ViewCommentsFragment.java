package com.example.shutter.Utils;


import android.content.Context;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shutter.Models.Comment;
import com.example.shutter.Models.Like;
import com.example.shutter.Models.Photo;
import com.example.shutter.Models.UserAccountSettings;
import com.example.shutter.Models.Users;
import com.example.shutter.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ViewCommentsFragment extends Fragment {

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FireBaseMethods mFirebaseMethods;

    //widgets
    private ImageView mBackArrow, mCheckMark;
    private EditText mComment;
    private ListView mListView; // ArrayList that hold all the comments

private  Context context;
    //vars
    private Photo mPhoto;
    private ArrayList<Comment> mComments;

    private static final String TAG = "ViewCommentsFragment";
    public ViewCommentsFragment(){
        super();
        setArguments(new Bundle());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_view_comments,container,false);
        mBackArrow=(ImageView) view.findViewById(R.id.backArrow);
        mCheckMark=(ImageView) view.findViewById(R.id.ivPostComment);
        mComment=(EditText) view.findViewById(R.id.comment);
        mListView=(ListView) view.findViewById(R.id.listView);
        mComments=new ArrayList<>();
context=getActivity();

try {
    mPhoto = getPhotoFromBundle();
setupFirebaseAuth(); // calling was on wron gplace before
}
catch (NullPointerException   ex)
{
    Log.e(TAG, "onCreateView: "+ex.getMessage() );

}
        setupWidgets();
        return view;
    }

    private void  setupWidgets()
    {

        for (int i=0;i<mComments.size();i++)
        {
            Log.d(TAG, "onCreateView: checking arraylist"+mComments.get(i));
        }
        CommentListAdapter adapter = new CommentListAdapter(context,
                R.layout.layout_comment, mComments);
        mListView.setAdapter(adapter);

        mCheckMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mComment.getText().toString().equals(""))
                {
                    Log.d(TAG, "onClick: submitting a new comment..");
                    addNewComment(mComment.getText().toString());

                    mComment.setText(" ");
                    /*Next task after adding the comments close the keyboard */
                    closeKeyBoard();

                }
                else
                {
                    Toast.makeText(getActivity(),"Post comments cannot be posted blank",Toast.LENGTH_LONG).show();
                }
            }
        });
        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: back to view post fragment");
//                if(getCallingActivityFromBundle().equalsIgnoreCase(getString(R.string.home_activity))==true){
//                    Log.d(TAG, "getCallingActivityFromBundle: getString(R.string.home_activity) "+getString(R.string.home_activity));
//                    getActivity().getSupportFragmentManager().popBackStack();
//                }

//                else {
//                    getActivity().getSupportFragmentManager().popBackStack();
//                }

                getActivity().getSupportFragmentManager().popBackStack();


            }
        });

    }

    // keyboard will close after adding the comments
    private  void closeKeyBoard()
    {
        View view=getActivity().getCurrentFocus();
        if (view!=null)
        {
            InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
        else
        {

        }

    }


    // get photo from the incoming bundle from profileActivity interface
    private Photo getPhotoFromBundle()
    {
        Log.d(TAG, "getPhotoBundle: arguments:"+getArguments());
        Bundle bundle =this.getArguments();
        if (bundle!=null)
        {
            Log.d(TAG, "getPhotoFromBundle: got photo"+bundle);
            return bundle.getParcelable(getString(R.string.Photo));
        }
        else
        {
            return null;
        }
    }

    private String getCallingActivityFromBundle()
    {
        Log.d(TAG, "getPhotoBundle: arguments:"+getArguments());
        Bundle bundle =this.getArguments();
        if (bundle!=null)
        {
            Log.d(TAG, "getCallingActivityFromBundle: getString(R.string.home_activity) "+getString(R.string.home_activity));

            return bundle.getString(getString(R.string.home_activity).toString());
        }
        else
        {
            return null;
        }
    }
    private String getTimestamp(){
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
    }
    private void addNewComment(String newComment)
    {
        Log.d(TAG, "addNewComment: aaddddding a new comment"+newComment);
        String commentId=myRef.push().getKey();

        Comment comment = new Comment();
        comment.setComment(newComment);
        comment.setDate_created(getTimestamp());
        comment.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        //storing  selected photo comments into the firebase node named photos which has a pic with picid and
        // under pic id we will have a new field called comments to store comments
        myRef.child(getString(R.string.dbname_photos))
                .child(mPhoto.getPhoto_id())
        .child(getString(R.string.field_comments))
        .child(commentId)
        .setValue(comment);


        //insert into user_photo node
        myRef.child(getString(R.string.dbname_user_photos))
                .child(mPhoto.getUser_id())
                .child(mPhoto.getPhoto_id())
        .child(getString(R.string.field_comments))
        .child(commentId)
        .setValue(comment);
    }








    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        if (mPhoto.getComments().size()==0)
        {
            mComments.clear();
            Comment firstComment = new Comment();
            firstComment.setComment(mPhoto.getCaption());
            firstComment.setUser_id(mPhoto.getUser_id());
            firstComment.setDate_created(mPhoto.getDate_created());
            mPhoto.setComments(mComments);
            setupWidgets();
        }

        // ------------------------------------ Real Time Comments code starts from here with addChildEventListener(new ChildEventListener() -----------------------------------//
myRef.child(context.getString(R.string.dbname_photos))
.child(mPhoto.getPhoto_id())
.child(context.getString(R.string.field_comments))
.addChildEventListener(new ChildEventListener() {
    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        Query query = myRef.child(context.getString(R.string.dbname_photos))
                .orderByChild(context.getString(R.string.field_photo_id))
                .equalTo(mPhoto.getPhoto_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren())
                {
                    Photo photo = new Photo() ;
                    Map<String,Object> objectMap =(HashMap<String, Object>)singleSnapshot.getValue();

                    photo.setCaption(objectMap.get(context.getString(R.string.field_caption)).toString());
                    photo.setTags(objectMap.get(context.getString(R.string.field_tags)).toString());
                    photo.setPhoto_id(objectMap.get(context.getString(R.string.field_photo_id)).toString());
                    photo.setUser_id(objectMap.get(context.getString(R.string.field_user_id)).toString());
                    photo.setDate_created(objectMap.get(context.getString(R.string.field_date_created)).toString());
                    photo.setImage_path(objectMap.get(context.getString(R.string.field_image_path)).toString());

                    /*Get list of comments*/
mComments.clear();
                    Comment firstComment = new Comment();
                    firstComment.setComment(mPhoto.getCaption());
                    firstComment.setUser_id(mPhoto.getUser_id());
                    firstComment.setDate_created(mPhoto.getDate_created());

                    Log.d(TAG, "onCreateView: firstcomment,user id, date created "+firstComment.getComment()+" "
                            +firstComment.getUser_id()+" "+firstComment.getDate_created());

                    mComments.add(firstComment);

                    for (DataSnapshot dataSnapshot1: singleSnapshot.child(context.getString(R.string.field_comments)).getChildren())
                    {
                        Comment comment = new Comment();
                        comment.setUser_id(dataSnapshot1.getValue(Comment.class).getUser_id());
                        comment.setComment(dataSnapshot1.getValue(Comment.class).getComment());
                        comment.setDate_created(dataSnapshot1.getValue(Comment.class).getDate_created());
                        mComments.add(comment);
                    }

                    photo.setComments(mComments);
                    mPhoto=photo;


setupWidgets();

//                    List<Like> likeList = new ArrayList<>();
//                    for (DataSnapshot dataSnapshot1: singleSnapshot.child(getString(R.string.field_likes)).getChildren())
//                    {
//                        Like like = new Like();
//                        like.setUser_id(dataSnapshot1.getValue(Like.class).getUser_id());
//                        likeList.add(like);
//                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled");
            }
        });

    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});




    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
