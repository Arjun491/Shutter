package com.example.shutter.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.shutter.R;
import com.example.shutter.Utils.BottomNavigationViewHelper;
import com.example.shutter.Utils.FireBaseMethods;
import com.example.shutter.Utils.SectionStatePagerAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

public class AccountSettingsActivity extends AppCompatActivity {

    public static final String TAG="AccountSettingsActivity";
    private Context mContext;
    public static final int ACTIVITY_NUM =4;
    private ImageView backArrow;
    private ViewPager mViewPager;
    private RelativeLayout mRelativeLayout;

    public SectionStatePagerAdapter pagerAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsettings);
        Log.d(TAG, "onCreate: started.");
        mContext = AccountSettingsActivity.this;
        mViewPager=(ViewPager)findViewById(R.id.viewpager_container) ;
        mRelativeLayout =findViewById(R.id.relLayout1);

        setupBottomNaviagtionView();
        setupSettingsList();
        setupFragments();
        getIncomingIntent();


        backArrow =findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to profile activity");
                finish();
            }
        });

    }

    private void getIncomingIntent() {
        Intent intent= getIntent();

        //if there is an imageUrl attached as an extra, then it was chosen from the gallery/photo fragment
        if(intent.hasExtra(getString(R.string.selected_image))) {
            Log.d(TAG, "getIncomingIntent: new incoming image url");
            if(intent.getStringExtra(getString(R.string.return_to_fragment)).equals(getString(R.string.edit_profile_fragment))) {
                //set the new profile picture
                FireBaseMethods fireBaseMethods = new FireBaseMethods(AccountSettingsActivity.this);
                fireBaseMethods.uploadNewPhoto(getString(R.string.profile_photo), null, 0,
                        intent.getStringExtra(getString(R.string.selected_image)));
            }
        }
        if (intent.hasExtra(getString(R.string.calling_activity))) {
            Log.d(TAG, "getIncomingIntent: received incoming intent from " + getString(R.string.profile_activity));
            setViewPager(pagerAdapter.getFragmentNumber(getString(R.string.edit_profile_fragment)));
        }
    }

    /*
     * Bottom Navigation setup
     * */

    private void setupBottomNaviagtionView()
    {
        Log.d(TAG, "setupBottomNaviagtionView: setting up bottomNaviagtionView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx)findViewById(R.id.bottomNavViewBar);

        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);

        BottomNavigationViewHelper.enableNavigation(mContext,this,bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = ((Menu) menu).getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }


    private void setupFragments()
    {
        pagerAdapter = new SectionStatePagerAdapter(getSupportFragmentManager()); // fragment at index 0
        pagerAdapter.addFragment(new EditProfileFragment(),getString(R.string.edit_profile_fragment));// fragment at index 1
        pagerAdapter.addFragment(new SignOutFragment(),getString(R.string.sign_out_fragment));
    }

    public void setViewPager(int fragmentNumber)
    {
        // responsible for navigation to fragments
        mRelativeLayout.setVisibility(View.GONE);
        Log.d(TAG, "setViewPager: navigating to fragment #:"+fragmentNumber);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(fragmentNumber);

    }


    private void setupSettingsList()
    {
        Log.d(TAG, "setupSettingsList: initaliazing the account settings list");

        final ListView listView =(ListView)findViewById(R.id.lvAccountSettings);

        ArrayList<String> options = new ArrayList<String>();

        options.add(getString(R.string.edit_profile_fragment));// fragment at index 0

        options.add(getString(R.string.sign_out_fragment));// fragment at index 1

        ArrayAdapter adapter =  new ArrayAdapter(

                mContext, android.R.layout.simple_list_item_1,options

        );
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setViewPager(position);

            }
        });

    }



}
