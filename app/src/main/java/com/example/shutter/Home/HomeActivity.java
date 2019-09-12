package com.example.shutter.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.shutter.Login.LoginActivity;
import com.example.shutter.Models.Photo;
import com.example.shutter.Models.UserAccountSettings;
import com.example.shutter.OnBoarding.IntroActivity;
import com.example.shutter.R;
import com.example.shutter.Utils.MainfeedListAdapter;
import com.example.shutter.Utils.ViewCommentsFragment;
import com.example.shutter.Utils.BottomNavigationViewHelper;
import com.example.shutter.Utils.SectionPagerAdapter;
import com.example.shutter.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class HomeActivity extends AppCompatActivity implements MainfeedListAdapter.OnLoadMoreItemsListener {



    @Override
    public void onLoadMoreItems() {
        Log.d(TAG,"onLoadMoreItems: displaying more photos");
        HomeFragment fragment =(HomeFragment)getSupportFragmentManager().
                findFragmentByTag("android:switcher: " + R.id.viewpager_container + ":" +mViewPager.getCurrentItem());

        if (fragment != null){
            fragment.displayMorePhotos();
        }
    }

    private static final String TAG = "HomeActivity";
    private Context  mContext = HomeActivity.this;
    public static final int ACTIVITY_NUM=0;
    private static final int Home_Fragment = 1;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    //widgets
    private ViewPager mViewPager;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: starting");


        mViewPager = (ViewPager)findViewById(R.id.viewpager_container);
        mFrameLayout = (FrameLayout)findViewById(R.id.container);
        mRelativeLayout = (RelativeLayout)findViewById(R.id.relLayoutParent);

        // mAuth.signOut();
        //Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        setupFirebaseAuth();


        initImageLoader();
        setupBottomNaviagtionView();
        setupViewPager();





    }

    public void onCommentThreadSelected(Photo photo,  String callingActivity ){
        ViewCommentsFragment fragment  = new ViewCommentsFragment();
        Log.d(TAG,"onCommentThreadSelected: selected a comment thread");
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.Photo),photo);

        args.putString(getString(R.string.home_activity),getString(R.string.home_activity));
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container,fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();

    }


    public void hideLayout(){
        Log.d(TAG,"hideLayout: hiding layout");
        mRelativeLayout.setVisibility(View.GONE);
        mFrameLayout.setVisibility(View.VISIBLE);
    }

    public void showLayout(){
        Log.d(TAG,"showLayout: showing layout");
        mRelativeLayout.setVisibility(View.VISIBLE);
        mFrameLayout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (mFrameLayout.getVisibility() == View.VISIBLE){
            showLayout();
        }
    }

    // intialize the imageloader
    private void initImageLoader()
    {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    /*
    * Responsible for adding 3 fragment tabs : camera, home and messages
    *
    * */

    private void setupViewPager()
    {
        SectionPagerAdapter adapter = new SectionPagerAdapter( getSupportFragmentManager());
        adapter.addFragment(new CameraFragment()); // index 0
        adapter.addFragment(new HomeFragment()); // index 1
        adapter.addFragment(new MessagesFragment()); // index 2


        mViewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_circle);
        tabLayout.getTabAt(1).setIcon(R.drawable.logo);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_arrow);

    }

    /*
    *
    * logo : https://www.google.com/search?tbm=isch&sa=1&ei=PEspXfesKvq_0PEPk8uN6AE&q=camera+shutter&oq=camera+shutter&gs_l=img.3..0l10.5631.6870..7164...0.0..0.116.466.6j1......0....1..gws-wiz-img.......0i7i30.Cflwqe2L_w0#imgrc=kfFKNaHGTS02LM:
    * */


    /*
    * Bottom Naviagtion setup
    * */

    private void setupBottomNaviagtionView()
    {
        Log.d(TAG, "setupBottomNaviagtionView: setting up bottomNaviagtionView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx)findViewById(R.id.bottomNavViewBar);

        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);

        BottomNavigationViewHelper.enableNavigation(mContext,this,bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = (menu).getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }


    //checking if user is logged in
    private void checkCurrentUser(FirebaseUser user)
    {
        Log.d(TAG, "checkCurrentUser: checking if user is logged in:");
        if(user == null)
        {

            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
            finish();

        }

    }


    /*setup the Firebase auth object*/
    private void setupFirebaseAuth()
    {
        Log.d(TAG, "setupFirebaseAuth: stetting up firebase auth..");
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override


            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if user is logged in
                checkCurrentUser(user);
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
    }

    //onStart and onStop method //
    @Override
    public void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        mViewPager.setCurrentItem(Home_Fragment);
        //checkCurrentUser(mAuth.getCurrentUser());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener!=null)
        {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
