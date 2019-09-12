package com.example.shutter.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.example.shutter.Home.HomeActivity;
import com.example.shutter.Likes.LikesActivity;
import com.example.shutter.Profile.ProfileActivity;
import com.example.shutter.R;
import com.example.shutter.Search.SearchActivity;
import com.example.shutter.Share.ShareActivity;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class BottomNavigationViewHelper {
    private static final String TAG = "BottomNaviViewHelper";

    /*
     * Botton navigation view setup
     */
    public  static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx)
    {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
         bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
         bottomNavigationViewEx.enableShiftingMode(false);
         bottomNavigationViewEx.setTextVisibility(false);

    }

    // how to navigate between activities
    public static  void enableNavigation(final Context context, final Activity callingActivity, BottomNavigationViewEx viewEx)
    {
        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId())
                {
                    case R.id.ic_house:
                        Intent intent1 = new Intent(context, HomeActivity.class);
                        context.startActivity(intent1);

                        callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;

                        case R.id.ic_search:
                            Intent intent2 = new Intent(context, SearchActivity.class);
                            context.startActivity(intent2);
                            callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;

                        case R.id.ic_circle:

                            Intent intent3 = new Intent(context, ShareActivity.class);
                            context.startActivity(intent3);
                            callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;

                        case R.id.ic_alert:
                            Intent intent4 = new Intent(context, LikesActivity.class);
                            context.startActivity(intent4);
                            callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;

                        case R.id.ic_android:
                            Intent intent5 = new Intent(context, ProfileActivity.class);
                            context.startActivity(intent5);
                            callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                            break;
                }
                return false;
            }
        });

    }
}
