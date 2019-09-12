package com.example.shutter.Share;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.shutter.R;
import com.example.shutter.Utils.BottomNavigationViewHelper;
import com.example.shutter.Utils.Permissions;
import com.example.shutter.Utils.SectionPagerAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class ShareActivity extends AppCompatActivity {
    private static final String TAG = "ShareActivity";
    private Context mContext = ShareActivity.this;

    //constants
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;
    public static final int ACTIVITY_NUM =2;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_share);
        Log.d(TAG, "onCreate: started");

        if(checkPermissionsArray(Permissions.PERMISSIONS)) {
            setupViewPager();
        }

        else {
            verifyPermissions(Permissions.PERMISSIONS);
        }

        //setupBottomNavigationView();
    }

    /*
     * Return the current tab number
     * 0 = GalleryFragment
     * 1 = PhotoFragment
     */
    public int getCurrentTabNumber()
    {
        return mViewPager.getCurrentItem();
    }

    /*
     * Setup viewpager for managing the tabs
     */
    private void setupViewPager(){
        SectionPagerAdapter adapter=new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GalleryFragment());
        adapter.addFragment(new PhotoFragment());
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);

        mViewPager.setAdapter(adapter);
        TabLayout tabLayout= findViewById(R.id.tabsBottom);
        tabLayout.setupWithViewPager(mViewPager, true);
        tabLayout.getTabAt(0).setText(getString(R.string.gallery));
        tabLayout.getTabAt(1).setText(getString(R.string.Photo));
    }

    public int getTask() {
        Log.d(TAG, "getTask: " + getIntent().getFlags());
        return getIntent().getFlags();
    }
    /*
     *
     * Verify all the permissions passed to the array
     * @param permissions
     */
    public void verifyPermissions(String[] permissions) {
        Log.d(TAG, "verifyPermissions: verifying permissions");
        ActivityCompat.requestPermissions(ShareActivity.this, permissions, VERIFY_PERMISSIONS_REQUEST);
    }

    /*
     * Check an array of permissions
     * @param permissions
     * @return
     */
    public boolean checkPermissionsArray(String[] permissions) {
        Log.d(TAG, "checkPermissionsArray: checking permissions array");

        for(int i=0; i<permissions.length; i++){
            String check = permissions[i];
            if(!checkPermissions(check)) {
                return false;
            }
        }

        return true;
    }

    /*
     * Check single permission if it has been verified
     * @param permission
     * @return
     */
    public boolean checkPermissions(String permission) {
        Log.d(TAG, "checkPermissions: checking permission: " + permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(ShareActivity.this, permission);
        if(permissionRequest != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermissions: permission was not granted for: " + permission);
            return false;
        }

        else {
            Log.d(TAG, "checkPermissions: permission was not granted for: " + permission);
            return true;
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
