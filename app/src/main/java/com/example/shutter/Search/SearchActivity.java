package com.example.shutter.Search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.shutter.Models.Users;
import com.example.shutter.Profile.ProfileActivity;
import com.example.shutter.R;
import com.example.shutter.Utils.BottomNavigationViewHelper;
import com.example.shutter.Utils.UserListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private Context mContext = SearchActivity.this;
    public static final int ACTIVITY_NUM =1;



    //variables

    private EditText mSearchParam;
    private ListView mListView;


    private List<Users> mUserList;

    private UserListAdapter mAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_search);

        mSearchParam = findViewById(R.id.search);
        mListView = findViewById(R.id.listView);
        Log.d(TAG, "onCreate: started");
        hideSoftKeyboard();
        setupBottomNavigationView();
        initTextListner();
    }

private void initTextListner(){
        Log.d(TAG,"initTextListner: initializing");
        mUserList = new ArrayList<>();
        mSearchParam.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {


                String text = mSearchParam.getText().toString().toLowerCase(Locale.getDefault());
                searchForMatch(text);
            }
        });

}

    private void searchForMatch(String keyword){
        Log.d(TAG,"searchForMatch: searching for a match: " + keyword);
        mUserList.clear();
        if(keyword.length()==0){

        }
        else
        {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child(getString(R.string.dbname_users))
                    .orderByChild(getString(R.string.field_username)).equalTo(keyword);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){

                        Log.d(TAG,"onDataChange: found user:" + singleSnapshot.getValue(Users.class).toString());
                        mUserList.add(singleSnapshot.getValue(Users.class));

                        // update list
                        updateUsersList();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }



    private void updateUsersList(){

        Log.d(TAG,"upadateUserList: updating users list");
        mAdapter =  new UserListAdapter(SearchActivity.this,R.layout.layout_user_listitem, mUserList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG,"onItemClick:Selected user" + mUserList.get(position).toString());


                //updating user list

                Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
                intent.putExtra(getString(R.string.calling_activity),getString(R.string.search_activity));

                intent.putExtra(getString(R.string.intent_user),mUserList.get(position));
                startActivity(intent);


            }
        });

    }

private void hideSoftKeyboard(){
        if(getCurrentFocus() != null)
    {
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
    }

}



    private void setupBottomNavigationView()
    {
        Log.d(TAG, "setupBottomNavigationView: setting up bottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);

        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);

        BottomNavigationViewHelper.enableNavigation(mContext,this,bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = (menu).getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }
}
