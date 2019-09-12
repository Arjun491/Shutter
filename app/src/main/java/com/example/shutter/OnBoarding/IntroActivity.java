package com.example.shutter.OnBoarding;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.shutter.Login.LoginActivity;
import com.example.shutter.R;
import com.example.shutter.Utils.IntroViewPagerAdapter;
import com.example.shutter.Utils.ScreenItem;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private ViewPager screenPager;
    IntroViewPagerAdapter introViewPagerAdapter;
    TabLayout tabLayout;
    Button button,skipbutton;
    int position=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intro);


        //dot

        String[] page1={"Simply view and edit your profile, Following and","followers"};
        String[] page2={" Search, Your Friend, follow them in just on tap","By typing their user name"};

        String[] page3={"Shutter Feed"," shows the latest updates of their life, Stay connected","give a like or comment "};
        String[] page4={" Upload picture in real time ","show how cool you are","and inspire others"};
        //int viewss
        skipbutton=findViewById(R.id.Skipbutton);
        // tabLayout=findViewById(R.id.intotablayout);
        //fill List
        final List<ScreenItem> mList=new ArrayList<>();
        mList.add((new ScreenItem("View Your Profile",page1,R.drawable.onborading1)));
        mList.add((new ScreenItem("Search Your Friend",page2,R.drawable.oborading3_search)));
        mList.add((new ScreenItem("Shutter Feed",page3,R.drawable.onboarding2)));
        mList.add((new ScreenItem("Share your life",page4,R.drawable.onboarding4_share)));

        // Setup ViewPager
        screenPager=findViewById(R.id.Intro_viewPager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this,mList);
        screenPager.setAdapter(introViewPagerAdapter);
        DotsIndicator dotsIndicator = (DotsIndicator) findViewById(R.id.intotablayout);

        dotsIndicator.setViewPager(screenPager);


        skipbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadthescreen();
            }
        });

    }

    private void loadthescreen() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
