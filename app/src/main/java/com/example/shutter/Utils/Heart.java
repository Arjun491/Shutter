package com.example.shutter.Utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.example.shutter.Profile.AccountSettingsActivity;

public class Heart {
    private static final String TAG = "Heart";
    private ImageView heatWhite, heartRed;
    public static final DecelerateInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    public static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();

    public Heart(ImageView heatWhite, ImageView heartRed)
    {
        this.heatWhite=heatWhite;
        this.heartRed=heartRed;
    }
    public void toggleLike()
    {
        Log.d(TAG, "toggleLike: toggling <3");
        // represent the group of animation tht can played together
        AnimatorSet animatorSet = new AnimatorSet();

        if(heartRed.getVisibility()== View.VISIBLE)
        {
            Log.d(TAG, "toggleLike: toggling red off..");
            heartRed.setScaleX(0.1f);
            heartRed.setScaleY(0.1f);
            ObjectAnimator scaleDownY =ObjectAnimator.ofFloat(heartRed,"scaleY",1f,0f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(ACCELERATE_INTERPOLATOR);
            ObjectAnimator scaleDownX =ObjectAnimator.ofFloat(heartRed,"scaleX",1f,0f);
            scaleDownX.setDuration(300);
            scaleDownX.setInterpolator(ACCELERATE_INTERPOLATOR);
            heartRed.setVisibility(View.GONE);
            heatWhite.setVisibility(View.VISIBLE);
            animatorSet.playTogether(scaleDownY,scaleDownX);
        }


        else if(heartRed.getVisibility()== View.GONE)
        {
            Log.d(TAG, "toggleLike: toggling red On..");
            heartRed.setScaleX(0.1f);
            heartRed.setScaleY(0.1f);
            ObjectAnimator scaleDownY =ObjectAnimator.ofFloat(heartRed,"scaleY",0.1f,1f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(DECELERATE_INTERPOLATOR);
            ObjectAnimator scaleDownX =ObjectAnimator.ofFloat(heartRed,"scaleX",0.1f,1f);
            scaleDownX.setDuration(300);
            scaleDownX.setInterpolator(DECELERATE_INTERPOLATOR);
            heartRed.setVisibility(View.VISIBLE);
            heatWhite.setVisibility(View.GONE);
            animatorSet.playTogether(scaleDownY,scaleDownX);
        }
        animatorSet.start();
    }
}
