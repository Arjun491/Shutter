package com.example.shutter.Utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shutter.Login.LoginActivity;
import com.example.shutter.R;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback

{

    private Context context;
    private View view;

    public FingerprintHandler(Context context){

        this.context = context;

    }  public FingerprintHandler(){



    }


    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject){

        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);


    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {

        this.update("There was an Auth Error. " + errString, false);
        //Toast.makeText(context,"Authentication error",Toast.LENGTH_LONG).show();
        LoginActivity loginActivity = new LoginActivity();


    }

    @Override
    public void onAuthenticationFailed() {

        this.update("Auth Failed. ", false);

    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

        this.update("Error: " + helpString, false);


    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {

        this.update("You can now access the app.", true);





    }


    private void update(String s, boolean b) {

        TextView paraLabel = ((Activity)context).findViewById(R.id.paraLabelOfFingerPrint);
        ImageView imageView =  ((Activity)context).findViewById(R.id.fingerPrintImage);

        paraLabel.setText(s);

        if(b == false){

            paraLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            imageView.setImageResource(R.drawable.ic_finger_print_done_red);


        } else {

            paraLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            imageView.setImageResource(R.drawable.ic_finger_print_done);

        }

    }

}