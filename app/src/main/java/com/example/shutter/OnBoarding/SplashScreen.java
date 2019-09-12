package com.example.shutter.OnBoarding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shutter.Login.LoginActivity;
import com.example.shutter.R;

public class SplashScreen extends AppCompatActivity {
ImageView imageViewSplash;
Button getStartedbtn;
boolean firstStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //* Hides Notification Bar
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

  setContentView(R.layout.activity_splash_screen);
        imageViewSplash=findViewById(R.id.bgone);


        imageViewSplash.animate().scaleX(2).scaleY(3).rotationX(1).setDuration(5000).start();
        getStartedbtn=findViewById(R.id.btnget);


getStartedbtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

            startActivity(new Intent(getApplicationContext(), IntroActivity.class));
            finish();


    }
});
    }
}
